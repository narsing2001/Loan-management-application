package com.document.verification.service.consumer;
import com.document.verification.service.dto.DocumentUploadedEventDTO;
import com.document.verification.service.service.VerificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentUploadedConsumerTest {

    @Mock
    private VerificationService verificationService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DocumentUploadedConsumer consumer;

    @Test
    void consume_validMessage_shouldCallService() throws Exception {

        String message = "{\"documentId\":\"123\",\"applicantId\":\"456\"}";

        DocumentUploadedEventDTO event = new DocumentUploadedEventDTO();
        event.setDocumentId(UUID.randomUUID());

        when(objectMapper.readValue(message, DocumentUploadedEventDTO.class))
                .thenReturn(event);

        consumer.consume(message);

        verify(verificationService, times(1)).verifyDocument(event);
    }
    @Test
    void consume_invalidMessage_shouldHandleException() throws Exception {
        String message = "invalid-json";

        when(objectMapper.readValue(message, DocumentUploadedEventDTO.class))
                .thenThrow(new RuntimeException("Parsing error"));

        consumer.consume(message);
        verify(verificationService, never()).verifyDocument(any());
    }
}
