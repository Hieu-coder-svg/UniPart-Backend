package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findTop20ByUserIdOrderByCreatedAtAsc(String id);
}
