package vn.vinaacademy.notification.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.vinaacademy.notification.dto.NotificationCreateDTO;
import vn.vinaacademy.notification.dto.NotificationDTO;
import vn.vinaacademy.notification.entity.Notification;

import java.util.UUID;

public interface NotificationService {
    NotificationDTO createNotification(NotificationCreateDTO dto);
    Page<NotificationDTO> getUserNotificationsPaginated(Boolean read, Notification.NotificationType type, Pageable pageable);
    void markAsRead(UUID notificationId);
    void deleteNotification(UUID notificationId);
    void markReadAll();
}