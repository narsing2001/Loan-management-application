package com.example.LoanTypeStrategyInterest.globalexception;

import com.example.LoanTypeStrategyInterest.enums.enum1.DomesticCourseType;
import com.example.LoanTypeStrategyInterest.enums.enum1.InternationalCourseType;
import com.example.LoanTypeStrategyInterest.exception.*;
import com.example.LoanTypeStrategyInterest.response.ApiResponse;
import com.example.LoanTypeStrategyInterest.response.ErrorResponse;
import com.example.LoanTypeStrategyInterest.response.ErrorResponse1;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler1 {

    @ExceptionHandler(InvalidPrincipalException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPrincipalException(InvalidPrincipalException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Principal/Base_Amount Amount", ex.getMessage());
    }

    @ExceptionHandler(InvalidEmiException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEmiException(InvalidEmiException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid EMI", ex.getMessage());
    }

    @ExceptionHandler(InvalidTenureException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTenureException(InvalidTenureException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Tenure Years", ex.getMessage());
    }

    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPaymentException(InvalidPaymentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Payment Amount", ex.getMessage());
    }

    @ExceptionHandler(EmiTooLowException.class)
    public ResponseEntity<ErrorResponse> handleEmiTooLowException(EmiTooLowException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "EMI Too Low , must be greater than zero", ex.getMessage());
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife && ife.getTargetType() != null && ife.getTargetType().isEnum()) {

            String invalidValue = String.valueOf(ife.getValue());
            String fieldName = ife.getPath().isEmpty() ? "unknown" : ife.getPath().get(ife.getPath().size() - 1).getFieldName();
            if (ife.getValue() == null || invalidValue.isBlank() || invalidValue.equals("null")) {
                String guidance = resolveFieldGuidance(fieldName);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.failure(guidance, "INVALID_FIELD_FOR_LOAN_TYPE"));
            }

            String acceptedValues = Arrays.stream(ife.getTargetType().getEnumConstants())
                    .map(Object::toString).collect(Collectors.joining(", "));

            String guidance = resolveFieldGuidance(fieldName);
            String message = String.format("Invalid value '%s' for field '%s'. %s Accepted values are: [%s]",
                    invalidValue, fieldName, guidance, acceptedValues);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(message, "INVALID_ENUM_VALUE"));
        }

        // Malformed JSON
        String rootCause = cause != null ? cause.getMessage() : "Unknown parsing error";
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure(
                        "Malformed JSON request body. Cause: " + rootCause,
                        "INVALID_REQUEST_BODY"
                ));
    }

    private String resolveFieldGuidance(String fieldName) {
        return switch (fieldName) {
            case "domesticCourseType" -> String.format(
                    "Field 'domesticCourseType' is only valid for DOMESTIC loans. " +
                            "For INTERNATIONAL loans, use 'internationalCourseType' with one of: [%s].",
                    Arrays.stream(InternationalCourseType.values())
                            .map(Enum::name)
                            .collect(Collectors.joining(", "))
            );
            case "internationalCourseType" -> String.format(
                    "Field 'internationalCourseType' is only valid for INTERNATIONAL loans. " +
                            "For DOMESTIC loans, use 'domesticCourseType' with one of: [%s].",
                    Arrays.stream(DomesticCourseType.values())
                            .map(Enum::name)
                            .collect(Collectors.joining(", "))
            );
            default -> String.format("Field '%s' received an invalid value.", fieldName);
        };
    }


    @ExceptionHandler(InterestComputationException.class)
    public ResponseEntity<ErrorResponse1> handleInterestComputationException(InterestComputationException ex) {
        return buildErrorResponse1(HttpStatus.UNPROCESSABLE_ENTITY, "INTEREST_COMPUTATION_FAILED", ex.getMessage());
    }

    @ExceptionHandler(InvalidLoanParameterException.class)
    public ResponseEntity<ErrorResponse1> handleInvalidLoanParameterException(InvalidLoanParameterException ex) {
        return buildErrorResponse1(HttpStatus.BAD_REQUEST, "INVALID_EMPTY_LOAN_PARAMETERS", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse1> handleValidationException(MethodArgumentNotValidException ex) {
        return buildErrorResponse1(HttpStatus.BAD_REQUEST, "INVALID_EMPTY_LOAN_PARAMETERS", "Missing required fields in loan request");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Throwable cause = ex.getCause();
        while (cause != null) {
            if (cause instanceof InvalidStatusException ise) {
                ApiResponse<Void> errorResponse = ApiResponse.failure("Invalid status value: " + ise.getInvalidStatus(), "INVALID_STATUS");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            cause = cause.getCause();
        }
        ApiResponse<Void> errorResponse = ApiResponse.failure("Invalid value for parameter: " + ex.getName(), "INVALID_PARAMETER");
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse1> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        return buildErrorResponse1(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE", "Content type is not supported: " + ex.getContentType());
    }
    @ExceptionHandler(BinarySearchException.class)
    public ResponseEntity<ErrorResponse1> handleBinarySearchCalculationFailException(
            BinarySearchException ex) {
        return buildErrorResponse1(HttpStatus.UNPROCESSABLE_ENTITY, "BINARY_SEARCH_CALCULATION_FAILED", ex.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred: " + ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String error, String message) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .build();
        log.error("{}: {}", error, message);
        return ResponseEntity.status(status).body(errorResponse);
    }

    private ResponseEntity<ErrorResponse1> buildErrorResponse1(HttpStatus status, String errorCode, String message) {
        ErrorResponse1 errorResponse1 = ErrorResponse1.builder()
                .success(false)
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .errorCode(errorCode)
                .message(message)
                .build();
        log.error("{}: {}", errorCode, message);
        return ResponseEntity.status(status).body(errorResponse1);
    }





}
