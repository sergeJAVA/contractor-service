package com.example.contractor_service.service.rabbit;

import com.example.contractor_service.model.Contractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class SendMessageRabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendUpdatedContractor(Contractor updated) {
        String messageId = UUID.randomUUID().toString();

        rabbitTemplate.convertAndSend(updated, message -> {
            message.getMessageProperties().setMessageId(messageId);
            return message;
        });
        log.info("Contractor sent to the <<deals_contractor_queue>> queue");
    }

}
