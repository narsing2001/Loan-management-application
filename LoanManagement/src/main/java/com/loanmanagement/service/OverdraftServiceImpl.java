package com.loanmanagement.service;

import com.loanmanagement.constants.MessageConstants;
import com.loanmanagement.dto.OverdraftResponseDto;
import com.loanmanagement.entity.Overdraft;
import com.loanmanagement.enums.Status;
import com.loanmanagement.exception.InvalidAmountException;
import com.loanmanagement.exception.ResourceNotFoundException;
import com.loanmanagement.repository.OverdraftRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OverdraftServiceImpl implements OverdraftService {

    private final OverdraftRepository repo;

    @Value("${od.interest.rate}")
    private double interestRate;

    @Value("${od.limit.percentage}")
    private double limitPercent;

    @Value("${od.tenure.months}")
    private int tenureMonths;

    //Create Overdraft
    @Override
    public void createOD(Long loanId, BigDecimal loanAmount) {

        log.info("Creating OD for loanId: {}", loanId);

        if (repo.findByLoanId(loanId).isPresent()) {
            log.info("OD already exists for loanId: {}", loanId);
            return;
        }

        Overdraft od = new Overdraft();
        od.setLoanId(loanId);

        BigDecimal limit = loanAmount.multiply(BigDecimal.valueOf(limitPercent).divide(BigDecimal.valueOf(100)));

        od.setLimitAmount(limit);
        od.setUsedAmount(BigDecimal.ZERO);
        od.setInterestRate(interestRate);

        od.setStartDate(LocalDate.now());
        od.setEndDate(LocalDate.now().plusMonths(tenureMonths));

        od.setStatus(Status.ACTIVE);

        repo.save(od);

        log.info("OD created successfully for loanId: {}", loanId);
    }

    //Withdraw
    @Override
    public void withdraw(Long loanId, BigDecimal amount) {

        log.info("Withdraw request: loanId={}, amount={}", loanId, amount);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(MessageConstants.INVALID_AMOUNT);
        }

        Overdraft od = getActiveOD(loanId);

        BigDecimal available = od.getLimitAmount().subtract(od.getUsedAmount());

        if (amount.compareTo(available) > 0) {
            log.error("Limit exceeded for loanId: {}", loanId);
            throw new InvalidAmountException(MessageConstants.LIMIT_EXCEEDED);
        }

        od.setUsedAmount(od.getUsedAmount().add(amount));
        repo.save(od);

        log.info("Withdraw successful for loanId={}, amount={}", loanId, amount);
    }

    //Repay
    @Override
    public void repay(Long loanId, BigDecimal amount) {

        log.info("Repay request: loanId={}, amount={}", loanId, amount);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(MessageConstants.INVALID_AMOUNT);
        }

        Overdraft od = getActiveOD(loanId);

        if (amount.compareTo(od.getUsedAmount()) > 0) {
            log.error("Repay amount exceeds used for loanId: {}", loanId);
            throw new InvalidAmountException(MessageConstants.INVALID_AMOUNT);
        }
        od.setUsedAmount(od.getUsedAmount().subtract(amount));
        repo.save(od);

        log.info("Repay successful for loanId={}, amount={}", loanId, amount);
    }

    //Get OD
    @Override
    public OverdraftResponseDto getOD(Long loanId) {

        log.info("Fetching OD details for loanId: {}", loanId);

        Overdraft od = repo.findByLoanId(loanId)
                .orElseThrow(() -> {
                    log.error("OD not found for loanId: {}", loanId);
                    return new ResourceNotFoundException(MessageConstants.OD_NOT_FOUND);
                });

        OverdraftResponseDto dto = new OverdraftResponseDto();

        dto.setLoanId(od.getLoanId());
        dto.setUsedAmount(od.getUsedAmount());
        dto.setLimit(od.getLimitAmount());

        dto.setInterest(
                od.getUsedAmount()
                        .multiply(BigDecimal.valueOf(od.getInterestRate()))
                        .divide(BigDecimal.valueOf(1200), 2, RoundingMode.HALF_UP)
        );

        return dto;
    }

    //Calculate Interest
    @Override
    public BigDecimal calculateInterest(Long loanId) {

        log.info("Calculating interest for loanId: {}", loanId);

        Overdraft od = getActiveOD(loanId);

        return od.getUsedAmount()
                .multiply(BigDecimal.valueOf(od.getInterestRate()))
                .divide(BigDecimal.valueOf(1200), 2, RoundingMode.HALF_UP);
    }
    private void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid amount: {}", amount);
            throw new InvalidAmountException(MessageConstants.INVALID_AMOUNT);
        }
    }

    private Overdraft getActiveOD(Long loanId) {

        Overdraft od = repo.findByLoanId(loanId)
                .orElseThrow(() -> {
                    log.error("OD not found for loanId: {}", loanId);
                    return new ResourceNotFoundException(MessageConstants.OD_NOT_FOUND);
                });

        if (!Status.ACTIVE.equals(od.getStatus())) {
            log.error("OD not active for loanId: {}", loanId);
            throw new InvalidAmountException(MessageConstants.OD_NOT_ACTIVE);
        }

        if (LocalDate.now().isAfter(od.getEndDate())) {
            log.error("OD expired for loanId: {}", loanId);
            throw new InvalidAmountException(MessageConstants.OD_EXPIRED);
        }

        return od;
    }
}