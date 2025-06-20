package vn.vinaacademy.email.service;

import java.util.Map;

public interface EmailService {

    void sendEmail(String toEmail, String subject, String body, boolean enableHtml);

    void sendEmailWithoutMQ(String toEmail, String subject, String body, boolean enableHtml);

    void sendEmailMQ(String toEmail, String subject, String body, boolean enableHtml);

    void sendEmailWithTemplate(String toEmail, String subject, String templateName, Map<String, Object> variables);

    void sendPasswordResetEmail(String email, String token);

    void sendWelcomeEmail(String email, String fullName);

    void sendNotificationEmail(String email, String title, String message, String actionUrl, String actionText);

    void sendPaymentSuccessEmail(String email, String fullName, String orderId, String amount, String orderTime, String courseUrl);

    void sendPaymentFailedEmail(String email, String fullName, String orderId, String errorMessage, String orderTime, String retryUrl);
}
