package com.example.contractor_service.service.outbox;

import com.example.contractor_service.model.Contractor;
import com.example.contractor_service.model.outbox.MessageStatus;
import com.example.contractor_service.model.outbox.OutboxMessage;
import com.example.contractor_service.repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxMessageService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void saveContractor(Contractor contractor) {
        OutboxMessage message = OutboxMessage.builder()
                .messageId(UUID.randomUUID())
                .payload(writeAsString(contractor))
                .status(MessageStatus.PENDING)
                .sentAt(LocalDateTime.now())
                .build();

        outboxRepository.save(message);
    }

    private String writeAsString(Contractor contractor) {
        try {
            return objectMapper.writeValueAsString(contractor);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize contractor", e);
        }
    }

}
