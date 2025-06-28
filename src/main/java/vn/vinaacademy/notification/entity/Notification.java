package vn.vinaacademy.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.vinaacademy.common.entity.SoftDeleteEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification extends SoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "content")
    private String content;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Column(name = "read_at", nullable = true)
    private LocalDateTime readAt;

    @Column(name = "recipient_id")
    private UUID recipientId;

    @Column(name = "target_url")
    private String targetUrl;

    @Column(name = "title")
    private String title;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NotificationType type = NotificationType.SYSTEM;

    public enum NotificationType {
        SYSTEM,
        PAYMENT_SUCCESS,
        COURSE_REVIEW,
        COURSE_APPROVAL,
        SUPPORT_REPLY,
        PROMOTION,
        FINANCIAL_ALERT,
        STAFF_REQUEST,
        INSTRUCTOR_REQUEST

    }
}	
