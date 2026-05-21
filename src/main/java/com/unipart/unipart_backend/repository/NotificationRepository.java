package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(String userId);
    Page<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    long countByUserIdAndIsReadFalse(String userId);
    boolean existsByUserIdAndTitleAndCreatedAtAfter(String userId, String title, LocalDateTime createdAt);
}
