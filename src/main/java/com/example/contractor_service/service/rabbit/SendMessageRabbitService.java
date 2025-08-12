package com.example.contractor_service.service.rabbit;

import com.example.contractor_service.model.Contractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SendMessageRabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendUpdatedContractor(Contractor updated) {
        rabbitTemplate.convertAndSend(updated);
        log.info("Contractor sent to the <<deals_dead_contractor_queue>> queue");
    }

}
