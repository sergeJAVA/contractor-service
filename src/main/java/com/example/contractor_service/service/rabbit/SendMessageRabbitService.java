package com.example.contractor_service.service.rabbit;

import com.example.contractor_service.model.Contractor;
import com.example.contractor_service.model.outbox.MessageStatus;
import com.example.contractor_service.model.outbox.OutboxMessage;
import com.example.contractor_service.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SendMessageRabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelayString = "${schedule.delay:5000}")
    @Transactional
    public void sendUpdatedContractor() {
        List<OutboxMessage> messages = outboxRepository
                .findTop10ByStatusOrderBySentAtAsc(MessageStatus.PENDING);

        if (messages.isEmpty()) {
            return;
        }

        for (OutboxMessage outboxMessage : messages) {
            try {
                Contractor updated = objectMapper.readValue(outboxMessage.getPayload(), Contractor.class);
                rabbitTemplate.convertAndSend(updated, message ->  {
                    message.getMessageProperties().setMessageId(outboxMessage.getMessageId().toString());
                    return message;
                });

                outboxMessage.setStatus(MessageStatus.SENT);
                outboxMessage.setSentAt(LocalDateTime.now());
                outboxRepository.save(outboxMessage);

                log.info("Contractor sent to the <<deals_contractor_queue>> queue, messageId={}", outboxMessage.getMessageId());
            } catch (Exception e) {

                log.error("Failed to send outbox message {} to Rabbit", outboxMessage.getMessageId(), e);
                outboxMessage.setStatus(MessageStatus.FAILED);
                outboxRepository.save(outboxMessage);
            }
        }
    }

}
