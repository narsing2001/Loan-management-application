package com.loanmanagement.exception;
import com.loanmanagement.dto.ApiResponseDto;
import com.loanmanagement.enums.Status;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleInvalidAmountException() {

        InvalidAmountException ex = new InvalidAmountException("error");

        ResponseEntity<ApiResponseDto> response =
                handler.handleInvalid(ex);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals(Status.FAILED, response.getBody().getStatus());
    }

    @Test
    void handleNotFoundException() {

        ResourceNotFoundException ex =
                new ResourceNotFoundException("not found");

        ResponseEntity<ApiResponseDto> response =
                handler.handleNotFound(ex);

        assertEquals(404, response.getStatusCodeValue());
    }
    @Test
    void handleGlobalException() {

        Exception ex = new Exception("error");

        ResponseEntity<ApiResponseDto> response =
                handler.handleGlobal(ex);

        assertEquals(500, response.getStatusCodeValue());
    }
}
