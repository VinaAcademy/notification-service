package vn.vinaacademy.notification.observer;


import vn.vinaacademy.notification.dto.NotificationDTO;

public interface NotificationSubject {
    void addObserver(NotificationObserver observer);
    void removeObserver(NotificationObserver observer);
    void notifyObservers(NotificationDTO notification, String action);
}