package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.dto.request.NotificationCreationRequest;
import com.unipart.unipart_backend.dto.request.NotificationUpdateRequest;
import com.unipart.unipart_backend.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    void createNotification(NotificationCreationRequest request);
    NotificationResponse updateNotification(Long id, NotificationUpdateRequest request);
    Page<NotificationResponse> getMyNotifications(Pageable pageable);
    long countUnreadNotifications();
}
