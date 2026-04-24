package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.dto.request.NotificationCreationRequest;
import com.unipart.unipart_backend.dto.request.NotificationUpdateRequest;
import com.unipart.unipart_backend.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    void createNotification(NotificationCreationRequest request);
    NotificationResponse updateNotification(Long id, NotificationUpdateRequest request);
    List<NotificationResponse> getMyNotifications();
}
