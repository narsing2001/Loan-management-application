package com.document.verification.service.consumer;

import com.document.verification.service.dto.DocumentUploadedEventDTO;
import com.document.verification.service.service.VerificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentUploadedConsumer {
    private final VerificationService verificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${topic.document-uploaded}", groupId = "document-verification-group")
    public void consume(String message) {
        try {
            log.info("Received Kafka message {}", message);

            DocumentUploadedEventDTO event = objectMapper.readValue(message, DocumentUploadedEventDTO.class);

            verificationService.verifyDocument(event);

        } catch (Exception e) {

            log.error("Failed to process Kafka message", e);
        }
    }
}