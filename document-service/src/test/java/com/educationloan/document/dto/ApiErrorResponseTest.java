package com.educationloan.document.dto;
import com.educationloan.document.dto.ApiErrorResponseDTO.ApiErrorResponse;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ApiErrorResponseTest {
    @Test
    void testBuilderAndGetters() {
        Instant now = Instant.now();
        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(now)
                .status(400)
                .error("Bad Request")
                .message("Invalid input")
                .build();
        assertEquals(now, response.getTimestamp());
        assertEquals(400, response.getStatus());
        assertEquals("Bad Request", response.getError());
        assertEquals("Invalid input", response.getMessage());
    }

    @Test
    void testSetters() {
        ApiErrorResponse response = new ApiErrorResponse();
        Instant now = Instant.now();
        response.setTimestamp(now);
        response.setStatus(500);
        response.setError("ERROR");
        response.setMessage("Failure");

        assertEquals(now, response.getTimestamp());
        assertEquals(500, response.getStatus());
        assertEquals("ERROR", response.getError());
        assertEquals("Failure", response.getMessage());
    }
    @Test
    void testAllArgsConstructor() {
        Instant now = Instant.now();
        ApiErrorResponse response = new ApiErrorResponse(now, 404, "NOT_FOUND", "Missing");
        assertEquals(now, response.getTimestamp());
        assertEquals(404, response.getStatus());
        assertEquals("NOT_FOUND", response.getError());
        assertEquals("Missing", response.getMessage());
    }
    @Test
    void testEqualsAndHashCode() {
        Instant now = Instant.now();
        ApiErrorResponse r1 = new ApiErrorResponse(now, 400, "ERR", "msg");
        ApiErrorResponse r2 = new ApiErrorResponse(now, 400, "ERR", "msg");
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }
    @Test
    void testNotEquals() {
        ApiErrorResponse r1 = new ApiErrorResponse();
        r1.setStatus(400);
        ApiErrorResponse r2 = new ApiErrorResponse();
        r2.setStatus(500);
        assertNotEquals(r1, r2);
    }
    @Test
    void testToString() {
        ApiErrorResponse response = new ApiErrorResponse();
        response.setStatus(400);
        response.setError("ERROR");
        String str = response.toString();
        assertNotNull(str);
        assertTrue(str.contains("ERROR"));
    }
    @Test
    void testNullValues() {
        ApiErrorResponse response = new ApiErrorResponse();
        response.setError(null);
        response.setMessage(null);
        assertNull(response.getError());
        assertNull(response.getMessage());
    }
}