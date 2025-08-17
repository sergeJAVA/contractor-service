package com.example.contractor_service.service.rabbit;

import com.example.contractor_service.model.Contractor;
import com.example.contractor_service.model.outbox.MessageStatus;
import com.example.contractor_service.model.outbox.OutboxMessage;
import com.example.contractor_service.service.outbox.OutboxMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SendMessageRabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    private final OutboxMessageService outboxMessageService;

    @Transactional
    public void sendUpdatedContractor(Contractor updated) {
        OutboxMessage outboxMessage = null;
        try {
            outboxMessage = outboxMessageService.createOutboxMessage(updated);
            outboxMessageService.saveMessage(outboxMessage);

            String messageId = outboxMessage.getMessageId().toString();
            rabbitTemplate.convertAndSend(updated, message -> {
                message.getMessageProperties().setMessageId(messageId);
                return message;
            });
            log.info("Contractor sent to the <<deals_contractor_queue>> queue, messageId={}", messageId);

            outboxMessage.setStatus(MessageStatus.SENT);
            outboxMessageService.saveMessage(outboxMessage);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize Contractor for OutboxMessage", e);
            if (outboxMessage != null) {
                outboxMessage.setStatus(MessageStatus.FAILED);
                outboxMessageService.saveMessage(outboxMessage);
            }
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("Failed to send Contractor to queue, messageId={}",
                    outboxMessage != null ? outboxMessage.getMessageId() : null, e);
            if (outboxMessage != null) {
                outboxMessage.setStatus(MessageStatus.FAILED);
                outboxMessageService.saveMessage(outboxMessage);
            }
        }
    }

}
