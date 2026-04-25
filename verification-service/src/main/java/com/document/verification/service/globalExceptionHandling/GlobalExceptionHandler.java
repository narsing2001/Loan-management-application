package com.document.verification.service.globalExceptionHandling;
import com.document.verification.service.dto.ApiErrorResponseDTO.*;
import com.document.verification.service.globalExceptionHandling.*;
import com.document.verification.service.globalExceptionHandling.customException.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<ApiErrorResponse> buildResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(
                ApiErrorResponse.builder()
                        .timestamp(Instant.now())
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .message(message)
                        .build());
    }
    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidFile(InvalidFileException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(OCRException.class)
    public ResponseEntity<ApiErrorResponse> handleOCRException(OCRException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(S3DownloadException.class)
    public ResponseEntity<ApiErrorResponse> handleS3Download(S3DownloadException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(VerificationNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(VerificationNotFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ParserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleParsing(ParserNotFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}



