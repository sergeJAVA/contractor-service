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
    public void saveMessage(OutboxMessage message) {
        outboxRepository.save(message);
    }

    public OutboxMessage createOutboxMessage(Contractor contractor) throws JsonProcessingException {
        return OutboxMessage.builder()
                .messageId(UUID.randomUUID())
                .payload(objectMapper.writeValueAsString(contractor))
                .status(MessageStatus.PENDING)
                .sentAt(LocalDateTime.now())
                .build();
    }

}
