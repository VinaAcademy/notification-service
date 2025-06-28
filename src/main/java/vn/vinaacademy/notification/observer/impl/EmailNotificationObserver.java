package vn.vinaacademy.notification.observer.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.vinaacademy.notification.dto.NotificationDTO;
import vn.vinaacademy.notification.mq.redis.EmailProducer;
import vn.vinaacademy.notification.observer.NotificationObserver;
import vn.vinaacademy.notification.service.EmailService;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailNotificationObserver implements NotificationObserver {

    private final EmailService emailService;

    @Value("${notifications.email.enabled:false}")
    private Boolean emailNotificationsEnabled;

    @Override
    public void onNotificationCreated(NotificationDTO notification) {
        // Only send email for specific notification types or based on settings
        if (emailNotificationsEnabled) {
            try {
                String emailContent = notification.getContent();
                String subject = String.format("Thông báo mới: %s - VinaAcademy", notification.getTitle());

                emailService.sendNotificationEmail(notification.getEmail(), subject, emailContent,
                        notification.getTargetUrl(), "Xem thông báo");
            } catch (Exception e) {
                log.error("Failed to send notification email", e);
            }
        }
    }

    @Override
    public void onNotificationRead(NotificationDTO notification) {
        // Usually no need to send emails for read notifications
    }

    @Override
    public void onNotificationDeleted(NotificationDTO notification) {
        // Usually no need to send emails for deleted notifications
    }
}