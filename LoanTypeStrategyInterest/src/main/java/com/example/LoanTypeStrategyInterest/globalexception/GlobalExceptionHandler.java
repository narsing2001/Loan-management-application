package com.example.LoanTypeStrategyInterest.globalexception;

import com.example.LoanTypeStrategyInterest.exception.AdmissionLetterVerificationException;
import com.example.LoanTypeStrategyInterest.exception.InstituteNotApprovedException;
import com.example.LoanTypeStrategyInterest.exception.InvalidTenureYearException;
import com.example.LoanTypeStrategyInterest.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle custom exception
    @ExceptionHandler(InstituteNotApprovedException.class)
    public ResponseEntity<ApiResponse<Void>> handleInstituteNotApproved(InstituteNotApprovedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(ex.getMessage(), "INSTITUTE_NOT_APPROVED"));
    }

    // Handle InvalidTenureYearException
    @ExceptionHandler(InvalidTenureYearException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidTenureYear(InvalidTenureYearException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(ex.getMessage(), "INVALID_TENURE_YEAR"));
    }

    // Handle validation errors (from @Valid annotations)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage()).collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.failure(errors, "VALIDATION_ERROR"));
    }
    @ExceptionHandler(AdmissionLetterVerificationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAdmissionLetterVerification(AdmissionLetterVerificationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(ex.getMessage(), "ADMISSION_LETTER_VERIFICATION_FAILED"));
    }
    // Handle generic exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("An unexpected error occurred: " + ex.getMessage(), "GENERIC_ERROR"));
    }
}