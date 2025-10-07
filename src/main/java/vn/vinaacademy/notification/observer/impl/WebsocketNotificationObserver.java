package vn.vinaacademy.notification.observer.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import vn.vinaacademy.notification.dto.NotificationDTO;
import vn.vinaacademy.notification.observer.NotificationObserver;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebsocketNotificationObserver implements NotificationObserver {

  private final SimpMessagingTemplate messagingTemplate;

  @Override
  public void onNotificationCreated(NotificationDTO notification) {
    try {
      messagingTemplate.convertAndSendToUser(notification.getUserId(), "/queue/notifications",
          notification);
    } catch (Exception e) {
      log.error("Failed to send websocket notification", e);
    }
  }

  @Override
  public void onNotificationRead(NotificationDTO notification) {

  }

  @Override
  public void onNotificationDeleted(NotificationDTO notification) {

  }
}
