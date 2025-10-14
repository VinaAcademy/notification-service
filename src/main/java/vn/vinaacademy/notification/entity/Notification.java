package vn.vinaacademy.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.vinaacademy.common.entity.SoftDeleteEntity;

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
    MESSAGE,
    PAYMENT_SUCCESS,
    COURSE_REVIEW,
    COURSE_APPROVAL,
    SUPPORT_REPLY,
    PROMOTION,
    FINANCIAL_ALERT,
    STAFF_REQUEST,
    INSTRUCTOR_REQUEST
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    Notification that = (Notification) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + Objects.hashCode(id);
    return result;
  }
}
