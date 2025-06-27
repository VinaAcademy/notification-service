package vn.vinaacademy.email.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import vn.vinaacademy.email.service.EmailService;
import vn.vinaacademy.kafka.event.GenericEmailEvent;

import java.util.Map;

@Service
public class EmailEventListener {

    private final EmailService emailService;

    public EmailEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "${spring.kafka.consumer.topic:email-topic}", groupId = "${spring.kafka.consumer.group-id:email-group}")
    public void handleEmailEvent(GenericEmailEvent event) {
        Map<String, Object> data = event.getData();
        switch (event.getType()) {
            case VERIFICATION:
                emailService.sendVerificationEmail(
                        (String) data.get("email"),
                        (String) data.get("token")
                );
                break;
            case PASSWORD_RESET:
                emailService.sendPasswordResetEmail(
                        (String) data.get("email"),
                        (String) data.get("token")
                );
                break;

            case WELCOME:
                emailService.sendWelcomeEmail(
                        (String) data.get("email"),
                        (String) data.get("fullName")
                );
                break;

            case NOTIFICATION:
                emailService.sendNotificationEmail(
                        (String) data.get("email"),
                        (String) data.get("title"),
                        (String) data.get("message"),
                        (String) data.get("actionUrl"),
                        (String) data.get("actionText")
                );
                break;

            case PAYMENT_SUCCESS:
                emailService.sendPaymentSuccessEmail(
                        (String) data.get("email"),
                        (String) data.get("fullName"),
                        (String) data.get("orderId"),
                        (String) data.get("amount"),
                        (String) data.get("orderTime"),
                        (String) data.get("courseUrl")
                );
                break;

            case PAYMENT_FAILED:
                emailService.sendPaymentFailedEmail(
                        (String) data.get("email"),
                        (String) data.get("fullName"),
                        (String) data.get("orderId"),
                        (String) data.get("errorMessage"),
                        (String) data.get("orderTime"),
                        (String) data.get("retryUrl")
                );
                break;

            default:
                throw new IllegalArgumentException("Unknown email event type: " + event.getType());
        }
    }
}
