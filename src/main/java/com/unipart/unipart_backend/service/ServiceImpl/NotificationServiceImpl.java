package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.dto.request.NotificationCreationRequest;
import com.unipart.unipart_backend.dto.request.NotificationUpdateRequest;
import com.unipart.unipart_backend.dto.response.NotificationResponse;
import com.unipart.unipart_backend.entity.Notification;
import com.unipart.unipart_backend.entity.User;
import com.unipart.unipart_backend.mapper.NotificationMapper;
import com.unipart.unipart_backend.repository.NotificationRepository;
import com.unipart.unipart_backend.repository.UserRepository;
import com.unipart.unipart_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    @Override
    public void createNotification(NotificationCreationRequest request) {
        Notification notification = notificationMapper.toEntity(request);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        NotificationResponse notificationResponse = notificationMapper.toResponse(notification);
        messagingTemplate.convertAndSendToUser(
                String.valueOf(request.getUserId()),
                "/queue/notifications",
                notificationResponse
        );
        notificationRepository.save(notification);
    }
    @Override
    public NotificationResponse updateNotification(Long id, NotificationUpdateRequest request){
        Notification existingNotification = notificationRepository.findById(id)
                .orElseThrow();
        notificationMapper.updateEntityFromDto(request, existingNotification);
        Notification updatedNotification = notificationRepository.save(existingNotification);
        NotificationResponse response = notificationMapper.toResponse(updatedNotification);
        messagingTemplate.convertAndSendToUser(
                String.valueOf(existingNotification.getUserId()),
                "/queue/notifications",
                response
        );
        return response;
    }
    @Override
    public Page<NotificationResponse> getMyNotifications(Pageable pageable){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User u = userRepository.findByUsername(name)
                .orElseThrow(() -> new com.unipart.unipart_backend.exception.AppException(com.unipart.unipart_backend.exception.ErrorCode.USER_NOT_EXIST));
        Page<Notification> page = notificationRepository.findByUserIdOrderByCreatedAtDesc(u.getId(), pageable);
        return page.map(notificationMapper::toResponse);
    }

    @Override
    public long countUnreadNotifications(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User u = userRepository.findByUsername(name)
                .orElseThrow(() -> new com.unipart.unipart_backend.exception.AppException(com.unipart.unipart_backend.exception.ErrorCode.USER_NOT_EXIST));
        return notificationRepository.countByUserIdAndIsReadFalse(u.getId());
    }
}
