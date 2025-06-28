package vn.vinaacademy.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.vinaacademy.notification.entity.Notification;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreateDTO {
    private String title;
    private String content;
    private String targetUrl;
    private Notification.NotificationType type;
    private UUID userId;
    
//    private String hash;  sau update len de hash value valid neu k se bi fake noti
}