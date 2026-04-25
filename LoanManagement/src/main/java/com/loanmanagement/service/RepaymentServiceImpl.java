package com.loanmanagement.service;

import com.loanmanagement.client.LoanClient;
import com.loanmanagement.constants.MessageConstants;
import com.loanmanagement.dto.*;
import com.loanmanagement.entity.RepaymentSchedule;
import com.loanmanagement.exception.InvalidAmountException;
import com.loanmanagement.exception.ResourceNotFoundException;
import com.loanmanagement.repository.RepaymentRepository;
import com.loanmanagement.strategy.RepaymentStrategy;
import com.loanmanagement.util.EmiCalculator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RepaymentServiceImpl implements RepaymentService {

    private final RepaymentRepository repository;
    private final OverdraftService overdraftService;
    private final LoanClient loanClient;
    private final @Qualifier("holidayStrategy") RepaymentStrategy holidayStrategy;
    private final @Qualifier("partPaymentStrategy") RepaymentStrategy partPaymentStrategy;

    @Value("${prepayment.charge.percent}")
    private BigDecimal prepaymentPercent;

    //GENERATE SCHEDULE
    @Override
    public void generateSchedule(Long loanId) {

        log.info("Generating schedule for loanId: {}", loanId);

        if (repository.existsByLoanId(loanId)) {
            log.error("Schedule already exists for loanId: {}", loanId);
            throw new InvalidAmountException(MessageConstants.SCHEDULE_EXISTS);
        }

        LoanDto loan = loanClient.getLoan(loanId);

        if (loan.getStartDate() == null) {
            loan.setStartDate(LocalDate.now());
        }
        overdraftService.createOD(loanId, loan.getPrincipal());

        int moratorium = loan.getMoratorium();
        int effectiveTenure = loan.getTenure() - moratorium;

        if (effectiveTenure <= 0) {
            log.error("Invalid tenure for loanId: {}", loanId);
            throw new InvalidAmountException(MessageConstants.INVALID_AMOUNT);
        }

        BigDecimal balance = loan.getPrincipal();

        BigDecimal rate = loan.getInterestRate()
                .divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);

        // MORATORIUM
        for (int m = 1; m <= moratorium; m++) {

            BigDecimal interest = balance.multiply(rate);
            balance = balance.add(interest);

            repository.save(RepaymentSchedule.builder()
                    .loanId(loanId)
                    .month(m)
                    .dueDate(loan.getStartDate().plusMonths(m))
                    .holiday(true)
                    .emi(BigDecimal.ZERO)
                    .interest(interest)
                    .principal(BigDecimal.ZERO)
                    .balance(balance)
                    .paid(false)
                    .prepaymentCharges(BigDecimal.ZERO)
                    .build());
        }

        // EMI
        BigDecimal emi = EmiCalculator.calculate(
                balance,
                loan.getInterestRate(),
                effectiveTenure
        );

        for (int m = moratorium + 1; m <= loan.getTenure(); m++) {

            BigDecimal interest = balance.multiply(rate);
            BigDecimal principal = emi.subtract(interest);
            balance = balance.subtract(principal);

            repository.save(RepaymentSchedule.builder()
                    .loanId(loanId)
                    .month(m)
                    .dueDate(loan.getStartDate().plusMonths(m))
                    .holiday(false)
                    .emi(emi)
                    .interest(interest)
                    .principal(principal)
                    .balance(balance)
                    .paid(false)
                    .prepaymentCharges(BigDecimal.ZERO)
                    .build());
        }

        log.info("Schedule generated successfully for loanId: {}", loanId);
    }

    //PAY EMI
    @Override
    public void payEmi(Long loanId, int month, BigDecimal amount) {

        log.info("Processing EMI payment: loanId={}, month={}, amount={}", loanId, month, amount);

        RepaymentSchedule current = repository.findByLoanIdAndMonth(loanId, month)
                .orElseThrow(() -> {
                    log.error("EMI not found for loanId={}, month={}", loanId, month);
                    return new ResourceNotFoundException(MessageConstants.EMI_NOT_FOUND);
                });

        if (current.isPaid()) {
            log.error("EMI already paid for loanId={}, month={}", loanId, month);
            throw new InvalidAmountException(MessageConstants.ALREADY_PAID);
        }

        BigDecimal emi = current.getEmi();

        if (amount.compareTo(emi) < 0) {
            log.error("Insufficient amount for loanId={}, month={}", loanId, month);
            throw new InvalidAmountException(MessageConstants.INVALID_AMOUNT);
        }

        current.setPaid(true);
        current.setPaymentDate(LocalDate.now());
        repository.save(current);

        BigDecimal extra = amount.subtract(emi);
        int nextMonth = month + 1;

        while (extra.compareTo(BigDecimal.ZERO) > 0) {

            RepaymentSchedule next = repository
                    .findByLoanIdAndMonth(loanId, nextMonth)
                    .orElse(null);

            if (next == null || next.isPaid()) break;

            BigDecimal nextEmi = next.getEmi();

            if (extra.compareTo(nextEmi) >= 0) {
                next.setPaid(true);
                next.setPaymentDate(LocalDate.now());
                extra = extra.subtract(nextEmi);
            } else {
                next.setEmi(nextEmi.subtract(extra));
                extra = BigDecimal.ZERO;
            }

            repository.save(next);
            nextMonth++;
        }

        log.info("EMI payment successful for loanId={}, month={}", loanId, month);
    }

    //HOLIDAY
    @Override
    public void markHoliday(Long loanId, int month) {

        log.info("Applying holiday for loanId={}, month={}", loanId, month);

        List<RepaymentSchedule> list =
                repository.findByLoanIdOrderByMonthAsc(loanId);

        if (list == null || list.isEmpty()) {
            log.error("Schedule not found for loanId: {}", loanId);
            throw new ResourceNotFoundException(MessageConstants.SCHEDULE_NOT_FOUND);
        }
        boolean exists = list.stream()
                .anyMatch(r -> r.getMonth() == month);

        if (!exists) {
            throw new InvalidAmountException(MessageConstants.INVALID_MONTH);
        }

        holidayStrategy.apply(list, loanId, month, BigDecimal.ZERO);
        repository.saveAll(list);

        log.info("Holiday applied successfully for loanId={}", loanId);
    }

    //PART PAYMENT
    @Override
    public void partPayment(PaymentRequestDto dto) {

        log.info("Part payment: loanId={}, amount={}, month={}",
                dto.getLoanId(), dto.getAmount(), dto.getMonth());
        List<RepaymentSchedule> list =
                repository.findByLoanIdOrderByMonthAsc(dto.getLoanId());

        if (list == null || list.isEmpty()) {
            log.error("Schedule not found for loanId: {}", dto.getLoanId());
            throw new ResourceNotFoundException(MessageConstants.SCHEDULE_NOT_FOUND);
        }
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(MessageConstants.INVALID_AMOUNT);
        }
        partPaymentStrategy.apply(
                list,
                dto.getLoanId(),
                dto.getMonth(),
                dto.getAmount()
        );
        repository.saveAll(list);

        log.info("Part payment applied successfully for loanId={}", dto.getLoanId());
    }

    //PREPAYMENT
    @Override
    public void prepayment(Long loanId) {

        log.info("Prepayment started for loanId: {}", loanId);

        List<RepaymentSchedule> list = repository.findByLoanIdOrderByMonthAsc(loanId);

        if (list.isEmpty()) {
            throw new ResourceNotFoundException(MessageConstants.SCHEDULE_NOT_FOUND);
        }

        LoanDto loan = loanClient.getLoan(loanId);

        int monthsPassed = (int) ChronoUnit.MONTHS.between(
                loan.getStartDate(),
                LocalDate.now()
        );

        if (loan.getLockInMonths() != 0 && monthsPassed < loan.getLockInMonths()) {
            throw new InvalidAmountException(MessageConstants.LOCK_IN_ACTIVE);
        }
        BigDecimal outstanding = list.stream()
                .filter(r -> !r.isPaid())
                .map(r -> r.getPrincipal().add(r.getInterest()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal charges = outstanding.multiply(prepaymentPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        boolean applied = false;

        for (RepaymentSchedule r : list) {

            if (!r.isPaid()) {

                r.setPaid(true);
                r.setPaymentDate(LocalDate.now());

                r.setEmi(BigDecimal.ZERO);
                r.setPrincipal(BigDecimal.ZERO);
                r.setInterest(BigDecimal.ZERO);
                r.setBalance(BigDecimal.ZERO);

                if (!applied) {
                    r.setPrepaymentCharges(charges);
                    applied = true;
                }
            }
        }

        repository.saveAll(list);

        log.info("Prepayment completed for loanId: {}", loanId);
    }
    //PrepaymentPreview
    @Override
    public PrepaymentPreview prepaymentPreview(Long loanId) {

        List<RepaymentSchedule> list = repository.findByLoanIdOrderByMonthAsc(loanId);

        RepaymentSchedule current = list.stream()
                .filter(r -> !r.isPaid())
                .findFirst()
                .orElseThrow(() -> new InvalidAmountException(MessageConstants.INVALID_AMOUNT));

        BigDecimal outstanding = current.getBalance();

        BigDecimal charges = outstanding.multiply(prepaymentPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal total = outstanding.add(charges);

        return new PrepaymentPreview(outstanding, charges, total);
    }

    //GET SCHEDULE
    @Override
    public ScheduleResponseDto getSchedule(Long loanId) {

        log.info("Fetching schedule for loanId: {}", loanId);

        List<RepaymentSchedule> list = repository.findByLoanIdOrderByMonthAsc(loanId);

        if (list.isEmpty()) {
            log.error("Schedule not found for loanId: {}", loanId);
            throw new ResourceNotFoundException(MessageConstants.SCHEDULE_NOT_FOUND);
        }

        List<RepaymentScheduleDto> dtoList =
                list.stream().map(this::convertToDto).toList();

        ScheduleResponseDto response = new ScheduleResponseDto();
        response.setLoanId(loanId);
        response.setEmis(dtoList);

        return response;
    }

    //GET EMI
    @Override
    public RepaymentScheduleDto getEmi(Long loanId, int month) {

        log.info("Fetching EMI for loanId={}, month={}", loanId, month);

        if (month <= 0) {
            log.error("Invalid month: {}", month);
            throw new InvalidAmountException(MessageConstants.INVALID_MONTH);
        }

        RepaymentSchedule r = repository.findByLoanIdAndMonth(loanId, month)
                .orElseThrow(() -> {
                    log.error("EMI not found for loanId={}, month={}", loanId, month);
                    return new ResourceNotFoundException(MessageConstants.EMI_NOT_FOUND);
                });

        return convertToDto(r);
    }

    private RepaymentScheduleDto convertToDto(RepaymentSchedule r) {

        RepaymentScheduleDto dto = new RepaymentScheduleDto();

        dto.setMonth(r.getMonth());
        dto.setEmi(r.getEmi());
        dto.setPrincipal(r.getPrincipal());
        dto.setInterest(r.getInterest());
        dto.setBalance(r.getBalance());
        dto.setPaid(r.isPaid());
        dto.setHoliday(r.isHoliday());
        dto.setDueDate(r.getDueDate());

        return dto;
    }
}