package vn.vinaacademy.email.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import vn.vinaacademy.common.config.UrlBuilder;
import vn.vinaacademy.common.config.UrlPath;
import vn.vinaacademy.common.exception.BadRequestException;
import vn.vinaacademy.email.config.MailProperties;
import vn.vinaacademy.email.dto.EmailMessage;
import vn.vinaacademy.email.entity.EmailAccountUsage;
import vn.vinaacademy.email.enums.EmailTemplate;
import vn.vinaacademy.email.mq.redis.EmailProducer;
import vn.vinaacademy.email.repository.EmailAccountUsageRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Log4j2
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    public static final String GMAIL_PROVIDER = "gmail";
    private final MailProperties mailProperties;

    private final EmailAccountUsageRepository emailAccountUsageRepository;

    @Autowired(required = false)
    private EmailProducer emailProducer;

    @Value("${spring.data.redis.enabled:false}")
    private boolean redisEnabled;

    private final UrlBuilder urlBuilder;

    private final TemplateEngine templateEngine;

    @Async("emailTaskExecutor")
    @Override
    public void sendEmail(String toEmail, String subject, String body, boolean enableHtml) {
        if (redisEnabled) {
            log.info("Sending email with redis mq...");
            sendEmailMQ(toEmail, subject, body, enableHtml);
        } else {
            log.info("Sending email direct....");
            sendEmailWithoutMQ(toEmail, subject, body, enableHtml);
        }
    }

    @Override
    public void sendEmailWithoutMQ(String toEmail, String subject, String body, boolean enableHtml) {
        if (mailProperties.isOnlyGmail()) {
            log.info("reset gmail count");
            emailAccountUsageRepository.resetEmailCountsIfNotUpdatedToday(LocalDate.now());
        }

        JavaMailSenderImpl mailSender = getSender();
        String sender = mailSender.getUsername();
        MimeMessage message = getMimeMessage(toEmail, subject, body, sender, enableHtml, mailSender);

        Instant start = Instant.now();

        mailSender.send(message);

        Instant end = Instant.now();
        long timeElapsed = Duration.between(start, end).toMillis();

        log.info("send smtp gmail takes {} ms", timeElapsed);
        if (mailProperties.isOnlyGmail()) {
            updateEmailUsage(sender);
        }
    }

    @Override
    public void sendEmailMQ(String toEmail, String subject, String body, boolean enableHtml) {
        EmailMessage emailMessage = new EmailMessage(toEmail, subject, body, enableHtml);
        emailProducer.enqueueEmail(emailMessage);
    }

    public void sendVerificationEmail(String email, String token) {
        String subject = "Xác thực tài khoản";
        String url = urlBuilder.buildActionUrl(UrlPath.VERIFY_ACCOUNT, token);

        Context context = new Context(LocaleContextHolder.getLocale());
        context.setVariable("verificationUrl", url);

        String body = parseTemplateToXhtml(context, EmailTemplate.VERIFY_ACCOUNT.getTemplateName());
        sendEmail(email, subject, body, true);
    }

    private String parseTemplateToXhtml(Context context, String templateName) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(UTF_8.name());

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        context.setVariable("frontEndUrl", urlBuilder.getFrontendUrl());
        return templateEngine.process(templateName, context);
    }

    public void sendEmailWithTemplate(String recipientEmail, String subject, String templateName, Map<String, Object> variables) {
        Context context = new Context(LocaleContextHolder.getLocale());
        if (variables != null) {
            variables.forEach(context::setVariable);
        }
        String body = parseTemplateToXhtml(context, templateName);
        sendEmail(recipientEmail, subject, body, true);
    }


    @Override
    public void sendPasswordResetEmail(String email, String token) {
        Map<String, Object> vars = Map.of("resetUrl", urlBuilder.buildActionUrl(UrlPath.RESET_PASSWORD, token));
        sendEmailWithTemplate(email, "Đặt lại mật khẩu", EmailTemplate.RESET_PASSWORD.getTemplateName(), vars);
    }

    @Override
    public void sendWelcomeEmail(String email, String fullName) {
        Map<String, Object> vars = Map.of(
                "userName", fullName,
                "exploreUrl", urlBuilder.getFrontendUrl() + UrlPath.EXPLORE.getPath()
        );
        sendEmailWithTemplate(email, "Chào mừng đến với VinaAcademy", EmailTemplate.WELCOME.getTemplateName(), vars);
    }

    @Override
    public void sendNotificationEmail(String email, String title, String message, String actionUrl, String actionText) {
        Map<String, Object> vars = Map.of(
                "userName", "bạn",
                "title", title,
                "message", message,
                "actionUrl", actionUrl,
                "actionText", actionText
        );
        sendEmailWithTemplate(email, title, EmailTemplate.NOTIFICATION.getTemplateName(), vars);
    }

    @Override
    public void sendPaymentSuccessEmail(String email, String fullName, String orderId, String amount, String orderTime, String courseUrl) {
        Map<String, Object> vars = Map.of(
                "userName", fullName,
                "orderId", orderId,
                "amount", amount,
                "orderTime", orderTime,
                "courseUrl", courseUrl
        );
        sendEmailWithTemplate(email, "Thanh toán thành công", EmailTemplate.PAYMENT_SUCCESS.getTemplateName(), vars);
    }

    @Override
    public void sendPaymentFailedEmail(String email, String fullName, String orderId, String errorMessage, String orderTime, String retryUrl) {
        Map<String, Object> vars = Map.of(
                "userName", fullName,
                "orderId", orderId,
                "errorMessage", errorMessage,
                "orderTime", orderTime,
                "retryUrl", retryUrl
        );
        sendEmailWithTemplate(email, "Thanh toán không thành công", EmailTemplate.PAYMENT_FAILED.getTemplateName(), vars);
    }


    private static MimeMessage getMimeMessage(String toEmail, String subject, String body, String sender, boolean enableHtml, JavaMailSenderImpl mailSender) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, enableHtml);
            helper.setFrom(sender);

            // image add
            helper.addInline("logoImage", new ClassPathResource("static/images/logo.png"));
//            helper.addInline("facebookImage", new ClassPathResource("static/images/facebook.png"));
//            helper.addInline("youtubeImage", new ClassPathResource("static/images/youtube.png"));
//            helper.addInline("linkedinImage", new ClassPathResource("static/images/linkedin.png"));
        } catch (Exception e) {
            log.error("Error creating mime message", e);
        }
        return message;
    }

    private static SimpleMailMessage getMailMessage(String toEmail, String subject, String body, String sender) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(sender);
        return message;
    }

    private JavaMailSenderImpl getSender() {
        List<String> providers = mailProperties.getActiveProviders();
        if (CollectionUtils.isEmpty(providers)) {
            throw BadRequestException.message("No mail providers provided.");
        }

        JavaMailSenderImpl mailSender = getMailSenderByProvider(providers);
        if (mailSender == null) {
            throw BadRequestException.message("No mail sender available for the specified provider.");
        }
        return mailSender;
    }

    private JavaMailSenderImpl getMailSenderByProvider(List<String> providers) {
        List<MailProperties.MailAccount> accounts = getEmailAccounts();

        List<MailProperties.MailAccount> filteredAccounts = filterByEmailUsage(providers, accounts);
        if (filteredAccounts.isEmpty()) {
            return null;
        }

        if (mailProperties.isUseRandom()) {
            MailProperties.MailAccount randomAccount = filteredAccounts.get(new Random().nextInt(filteredAccounts.size()));
            return createMailSender(randomAccount);
        }
        return createMailSender(filteredAccounts.get(0));
    }

    private List<MailProperties.MailAccount> filterByEmailUsage(List<String> providers, List<MailProperties.MailAccount> accounts) {
        //only apply limit for gmail
        if (mailProperties.isOnlyGmail()) {
            Map<String, EmailAccountUsage> usageMap = emailAccountUsageRepository.findAll().stream().collect(Collectors.toMap(EmailAccountUsage::getUsername, Function.identity(), (o, n) -> n));

            return accounts.stream().filter(acc -> providers.contains(acc.getProvider()) && hasRemainingQuota(acc.getUsername(), usageMap)).toList();
        }
        return accounts;
    }

    private List<MailProperties.MailAccount> getEmailAccounts() {
        if (CollectionUtils.isEmpty(mailProperties.getAccounts())) {
            throw BadRequestException.message("Missing account email configuration.");
        }
        if (mailProperties.isOnlyGmail()) {
            return mailProperties.getAccounts().stream().filter(s -> GMAIL_PROVIDER.equalsIgnoreCase(s.getProvider())).toList();
        }
        return mailProperties.getAccounts();
    }

    private JavaMailSenderImpl createMailSender(MailProperties.MailAccount account) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(account.getHost());
        mailSender.setPort(account.getPort());
        mailSender.setUsername(account.getUsername());
        mailSender.setPassword(account.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.putAll(account.getProperties());
        return mailSender;
    }

    private boolean hasRemainingQuota(String username, Map<String, EmailAccountUsage> usageMap) {
        return usageMap.getOrDefault(username, new EmailAccountUsage()).getEmailCount() < mailProperties.getDailyLimit();
    }

    private void updateEmailUsage(String username) {
        EmailAccountUsage usage = emailAccountUsageRepository.findByUsername(username).orElseGet(() -> {
            EmailAccountUsage newUsage = new EmailAccountUsage();
            newUsage.setUsername(username);
            newUsage.setEmailCount(0);
            newUsage.setLastResetDate(LocalDate.now());
            return newUsage;
        });

        usage.setEmailCount(usage.getEmailCount() + 1);
        usage.setLastSent(LocalDateTime.now());
        emailAccountUsageRepository.save(usage);
    }
}
