package com.example.LoanTypeStrategyInterest.controller;

import com.example.LoanTypeStrategyInterest.dto.dto1.InterestRateCalculationRequestDTO;
import com.example.LoanTypeStrategyInterest.dto.dto1.InterestRateCalculationResponseDTO;
import com.example.LoanTypeStrategyInterest.exception.InvalidLoanParameterException;
import com.example.LoanTypeStrategyInterest.service.serv1.InterestRateCalculationService;
import com.example.LoanTypeStrategyInterest.service.serv1.LoanInterestCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/interest")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Loan Interest Calculator",
        description = "APIs for calculating loan interest rates")
public class LoanInterestRateController {

    private final LoanInterestCalculationService loanInterestCalculationService;
    private final InterestRateCalculationService interestRateCalculationService;

    // LCS----------------------------------------------------------------------------------------
    @PostMapping(value = "/calculate-with-strategy",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Calculate interest rate using loan type strategy",
            description = "Calculates interest rate based on loan type, applicant details, and EMI using strategy pattern")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Interest rate calculated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = InterestRateCalculationResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters or validation error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error during calculation",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    public ResponseEntity<InterestRateCalculationResponseDTO> calculateLoanInterestWithStrategy(
            @Parameter(
                    description = "Loan interest calculation request containing loan type, principal amount, EMI, and tenure",
                    required = true,
                    schema = @Schema(implementation = InterestRateCalculationRequestDTO.class)
            ) @Valid @RequestBody InterestRateCalculationRequestDTO interestRateCalculationRequestDTO) {

        InterestRateCalculationResponseDTO response = loanInterestCalculationService.computeLoanInterestRate(interestRateCalculationRequestDTO);
        log.info("Interest rate calculation completed successfully");
        return ResponseEntity.ok(response);
    }

    // NRM---------------------------------------------------------------------------------------------------------------------
    @PostMapping(value = "/calculate-interest-rate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InterestRateCalculationResponseDTO> calculateInterestRate(
            @Valid @RequestBody InterestRateCalculationRequestDTO interestRateCalculationRequestDTO) {
        log.info("Received direct interest rate calculation request - Principal: {}, EMI: {}, Tenure: {} years",
                interestRateCalculationRequestDTO.getApprovedAmount(),
                interestRateCalculationRequestDTO.getMonthlyEmi(),
                interestRateCalculationRequestDTO.getTenureYears());
        InterestRateCalculationResponseDTO response = interestRateCalculationService.calculateInterestRate(interestRateCalculationRequestDTO);
        return ResponseEntity.ok(response);
    }

    // BSM--------------------------------------------------------------------------------------------------------------
    @PostMapping(value = "/calculate-interest-rate-binary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InterestRateCalculationResponseDTO> calculateInterestRateBinarySearch(
            @Valid @RequestBody InterestRateCalculationRequestDTO interestRateCalculationRequestDTO
    ) {
        log.info("Received binary search interest rate calculation request - Principal: {}, EMI: {}, Tenure: {} years",
                interestRateCalculationRequestDTO.getApprovedAmount(),
                interestRateCalculationRequestDTO.getMonthlyEmi(),
                interestRateCalculationRequestDTO.getTenureYears());
        InterestRateCalculationResponseDTO response = interestRateCalculationService.calculateInterestRateBinarySearch(interestRateCalculationRequestDTO);
        return ResponseEntity.ok(response);
    }

    // HCE---------------------------------------------------------------------------------------------------
    @GetMapping(value = "/health", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> healthCheck() {
        log.debug("Health check endpoint called");
        return ResponseEntity.ok("Loan Interest Calculation Service is running");
    }
}
