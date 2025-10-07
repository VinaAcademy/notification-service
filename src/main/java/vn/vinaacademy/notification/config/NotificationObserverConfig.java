package vn.vinaacademy.notification.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import vn.vinaacademy.notification.observer.NotificationSubject;
import vn.vinaacademy.notification.observer.impl.EmailNotificationObserver;
import vn.vinaacademy.notification.observer.impl.LoggingNotificationObserver;
import vn.vinaacademy.notification.observer.impl.WebsocketNotificationObserver;

@Configuration
@RequiredArgsConstructor
public class NotificationObserverConfig {

  private final NotificationSubject notificationPublisher;
  private final LoggingNotificationObserver loggingObserver;
  private final EmailNotificationObserver emailObserver;
  private final WebsocketNotificationObserver websocketObserver;

  @PostConstruct
  public void registerObservers() {
    notificationPublisher.addObserver(loggingObserver);
    notificationPublisher.addObserver(emailObserver);
    notificationPublisher.addObserver(websocketObserver);
  }
}