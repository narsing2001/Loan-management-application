package com.document.verification.service.dto.ApiErrorResponseDTO;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

public class ApiErrorResponseTest {
    @Test
    void testAllArgsConstructor() {
        Instant now = Instant.now();
        ApiErrorResponse response = new ApiErrorResponse(
                now,
                400,
                "Bad Request",
                "Invalid input"
        );

        assertEquals(now, response.getTimestamp());
        assertEquals(400, response.getStatus());
        assertEquals("Bad Request", response.getError());
        assertEquals("Invalid input", response.getMessage());
    }
    @Test
    void testBuilder() {
        Instant now = Instant.now();
        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(now)
                .status(500)
                .error("Internal Server Error")
                .message("Something went wrong")
                .build();
        assertNotNull(response);
        assertEquals(500, response.getStatus());
        assertEquals("Internal Server Error", response.getError());
    }

    @Test
    void testSettersAndGetters() {
        ApiErrorResponse response = new ApiErrorResponse();

        Instant now = Instant.now();
        response.setTimestamp(now);
        response.setStatus(404);
        response.setError("Not Found");
        response.setMessage("Resource not found");

        assertEquals(now, response.getTimestamp());
        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getError());
        assertEquals("Resource not found", response.getMessage());
    }
        @Test
        void testEqualsAndHashCode() {
            Instant now = Instant.now();

            ApiErrorResponse obj1 = new ApiErrorResponse(now, 400, "BAD_REQUEST", "Error occurred");
            ApiErrorResponse obj2 = new ApiErrorResponse(now, 400, "BAD_REQUEST", "Error occurred");
            assertEquals(obj1, obj2);
            assertEquals(obj1.hashCode(), obj2.hashCode());
        }

        @Test
        void testNotEquals() {
            ApiErrorResponse obj1 = new ApiErrorResponse(Instant.now(), 400, "BAD_REQUEST", "Error");
            ApiErrorResponse obj2 = new ApiErrorResponse(Instant.now(), 500, "INTERNAL_ERROR", "Failure");
            assertNotEquals(obj1, obj2);
        }

        @Test
        void testToString() {
            ApiErrorResponse obj = new ApiErrorResponse(Instant.now(), 400, "BAD_REQUEST", "Error");
            String result = obj.toString();
            assertNotNull(result);
            assertTrue(result.contains("BAD_REQUEST"));
        }

        @Test
        void testCanEqual() {
            ApiErrorResponse obj = new ApiErrorResponse();
            assertTrue(obj.canEqual(new ApiErrorResponse()));
        }
    }
