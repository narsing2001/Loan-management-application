package com.loanmanagement.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanmanagement.constants.MessageConstants;
import com.loanmanagement.dto.LoanDto;
import com.loanmanagement.exception.InvalidAmountException;
import com.loanmanagement.exception.LoanDataException;
import com.loanmanagement.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanClient {

    private final ObjectMapper mapper;

    public LoanDto getLoan(Long loanId) {

        log.info("Fetching loan for loanId: {}", loanId);

        if (loanId == null) {
            throw new InvalidAmountException(MessageConstants.LOAN_ID_REQUIRED);
        }

        try {
            ClassPathResource resource = new ClassPathResource("loans.json");

            if (!resource.exists()) {
                log.error("loans.json file not found in resources folder");
                throw new LoanDataException(MessageConstants.LOANS_FILE_NOT_FOUND);
            }

            try (InputStream inputStream = resource.getInputStream()) {

                List<LoanDto> loans = mapper.readValue(
                        inputStream,
                        new TypeReference<List<LoanDto>>() {}
                );

                log.info("Total loans loaded: {}", loans.size());

                LoanDto loan = loans.stream()
                        .filter(l -> l.getLoanId().equals(loanId))
                        .findFirst()
                        .orElseThrow(() -> {
                            log.error("Loan not found for loanId: {}", loanId);
                            return new ResourceNotFoundException(MessageConstants.LOAN_NOT_FOUND);
                        });

                if (loan.getStartDate() == null) {
                    loan.setStartDate(LocalDate.now());
                    log.info("StartDate was null, setting current date");
                }

                return loan;
            }

        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("Error reading loans.json", e);
            throw new LoanDataException(MessageConstants.LOAN_DATA_ERROR);
        }
    }
}