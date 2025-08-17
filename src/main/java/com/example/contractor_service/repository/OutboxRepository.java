package com.example.contractor_service.repository;

import com.example.contractor_service.model.outbox.MessageStatus;
import com.example.contractor_service.model.outbox.OutboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxMessage, UUID> {

    List<OutboxMessage> findTop10ByStatusOrderBySentAtAsc(MessageStatus status);

}
