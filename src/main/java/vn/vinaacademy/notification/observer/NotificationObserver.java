package vn.vinaacademy.notification.observer;


import vn.vinaacademy.notification.dto.NotificationDTO;

public interface NotificationObserver {
    void onNotificationCreated(NotificationDTO notification);
    void onNotificationRead(NotificationDTO notification);
    void onNotificationDeleted(NotificationDTO notification);
}