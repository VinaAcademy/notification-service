package vn.vinaacademy.notification.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import vn.vinaacademy.common.exception.BadRequestException;
import vn.vinaacademy.kafka.KafkaTopic;
import vn.vinaacademy.kafka.event.NotificationCreateEvent;
import vn.vinaacademy.notification.dto.NotificationCreateDTO;
import vn.vinaacademy.notification.dto.mapper.NotificationMapper;
import vn.vinaacademy.notification.service.NotificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NotificationService notificationService;

    @KafkaListener(topics = KafkaTopic.NOTIFICATION_TOPIC, groupId = "${spring.kafka.consumer.group-id:email-group}",
            containerFactory = "kafkaListenerContainerFactory")
    public void handleNotificationEvent(NotificationCreateEvent createEvent, Acknowledgment acknowledgment) {
        // Process the notification creation event
        NotificationCreateDTO notificationCreateDTO = NotificationMapper.INSTANCE.toDTO(createEvent);
        try {
            notificationService.createNotification(notificationCreateDTO);
        } catch (BadRequestException e) {
            // Log the error and do not acknowledge the message
            // This will allow the message to be retried later
            log.error("Error processing notification event: {}", e.getMessage());
            // Optionally, you can send an error notification or log it to a monitoring system
            return; // Exit the method without acknowledging
        } catch (Exception e) {
            // Log unexpected errors
            log.error("Unexpected error processing notification event: {}", e.getMessage(), e);
            // Optionally, you can send an error notification or log it to a monitoring system
            return; // Exit the method without acknowledging
        }
        // Acknowledge the message after processing
        acknowledgment.acknowledge();
    }
}
