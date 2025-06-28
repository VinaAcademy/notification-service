package vn.vinaacademy.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.vinaacademy.common.entity.BaseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "email_account_usage")
public class EmailAccountUsage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "email_count")
    private int emailCount;

    @Column(name = "last_sent")
    private LocalDateTime lastSent;

    @Column(name = "last_reset_date")
    private LocalDate lastResetDate;
}
