package vn.vinaacademy.notification.observer.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.vinaacademy.notification.dto.NotificationDTO;
import vn.vinaacademy.notification.observer.NotificationObserver;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingNotificationObserver implements NotificationObserver {
    
    @Override
    public void onNotificationCreated(NotificationDTO notification) {
        log.debug("Notification created: {}", notification.getId());
    }
    
    @Override
    public void onNotificationRead(NotificationDTO notification) {
        log.debug("Notification read: {}", notification.getId());
    }
    
    @Override
    public void onNotificationDeleted(NotificationDTO notification) {
        log.debug("Notification deleted: {}", notification.getId());
    }
}