package vn.vinaacademy.notification.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.vinaacademy.common.exception.BadRequestException;
import vn.vinaacademy.common.exception.UnauthorizedException;
import vn.vinaacademy.notification.constants.NotificationAction;
import vn.vinaacademy.notification.dto.NotificationCreateDTO;
import vn.vinaacademy.notification.dto.NotificationDTO;
import vn.vinaacademy.notification.dto.mapper.NotificationMapper;
import vn.vinaacademy.notification.entity.Notification;
import vn.vinaacademy.notification.entity.Notification.NotificationType;
import vn.vinaacademy.notification.observer.NotificationSubject;
import vn.vinaacademy.notification.repository.NotificationRepository;
import vn.vinaacademy.notification.service.NotificationService;
import vn.vinaacademy.security.authentication.SecurityContextHolder;
import vn.vinaacademy.security.grpc.UserGrpcClient;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserGrpcClient userGrpcClient;

  private final NotificationSubject notificationPublisher;

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public NotificationDTO createNotification(NotificationCreateDTO dto) {
    var user = userGrpcClient.getUserById(dto.getUserId());
    if (!user.getSuccess()) {
      throw BadRequestException.message("Người dùng không tồn tại");
    }
    var userInfo = user.getUser();
    Notification notification = NotificationMapper.INSTANCE.toEntity(dto);
    notification.setRecipientId(UUID.fromString(userInfo.getId()));
    notification.setCreatedAt(LocalDateTime.now());
    notification.setIsRead(dto.getType() == NotificationType.MESSAGE);
    notification = notificationRepository.save(notification);

    NotificationDTO result = NotificationMapper.INSTANCE.toDTO(notification);

    result.setEmail(userInfo.getEmail());
    result.setUserId(userInfo.getId());

    notificationPublisher.notifyObservers(result, NotificationAction.CREATE);
    return result;
  }

  // Lấy danh sách các notifications từ user lấy từ Auth backend thông
  // qua tokenAccess
  @Override
  public Page<NotificationDTO> getUserNotificationsPaginated(Boolean read,
      Notification.NotificationType type, Pageable pageable) {
    UUID currentUserId = UUID.fromString(SecurityContextHolder.getCurrentUserId());
    Page<Notification> page;

    if (type != null && read != null) {
      page = notificationRepository.findByRecipientIdAndTypeAndIsRead(currentUserId, type, read,
          pageable);
    } else if (type != null) {
      page = notificationRepository.findByRecipientIdAndType(currentUserId, type, pageable);
    } else if (read != null) {
      page = notificationRepository.findByRecipientIdAndIsRead(currentUserId, read, pageable);
    } else {
      page = notificationRepository.findByRecipientId(currentUserId, pageable);
    }

    return page.map(NotificationMapper.INSTANCE::toDTO);

  }

  //đánh dấu đã đọc cho noti
  @Override
  public void markAsRead(UUID notificationId) {
    Notification notification = findNotification(notificationId);
    UUID currentUserId = UUID.fromString(SecurityContextHolder.getCurrentUserId());
    if (!notification.getRecipientId().equals(currentUserId)) {
      throw UnauthorizedException.message("Xác thực thất bại vui lòng kiểm tra lại");
    }
    notification.setIsRead(true);
    notification.setReadAt(LocalDateTime.now());
    notificationRepository.save(notification);

    notificationPublisher.notifyObservers(NotificationMapper.INSTANCE.toDTO(notification),
        NotificationAction.READ);
  }

  //chuyển noti sang status bị xóa
  @Override
  public void deleteNotification(UUID notificationId) {
    Notification notification = findNotification(notificationId);
    UUID currentUserId = UUID.fromString(SecurityContextHolder.getCurrentUserId());
    if (!notification.getRecipientId().equals(currentUserId)) {
      throw UnauthorizedException.message("Xác thực thất bại vui lòng kiểm tra lại");
    }
    notificationRepository.delete(notification); // Perform hard deletion

    notificationPublisher.notifyObservers(NotificationMapper.INSTANCE.toDTO(notification),
        NotificationAction.DELETE);
  }

  public Notification findNotification(UUID id) {
    return notificationRepository.findById(id)
        .orElseThrow(() -> BadRequestException.message("Không tìm thấy thông báo"));
  }

  // đánh dấu đã đọc tất cả noti
  @Override
  @Transactional
  public void markReadAll() {
    UUID currentUserId = UUID.fromString(SecurityContextHolder.getCurrentUserId());
    List<Notification> unreadNotis = notificationRepository.findByIsReadAndRecipientId(false,
        currentUserId);

    if (unreadNotis.isEmpty()) {
      return;
    }
    List<UUID> notiIds = unreadNotis.stream().map(Notification::getId).toList();
    notificationRepository.markRead(notiIds); // marks in DB in a transaction

    unreadNotis.forEach(notification ->
        notificationPublisher.notifyObservers(
            NotificationMapper.INSTANCE.toDTO(notification),
            NotificationAction.READ
        )
    );
  }

}