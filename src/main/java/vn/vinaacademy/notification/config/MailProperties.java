package vn.vinaacademy.notification.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "mail")
@Data
@NoArgsConstructor
public class MailProperties {
    private List<MailAccount> accounts;
    private boolean useRandom;
    private int dailyLimit = 450;
    private List<String> activeProviders;
    private boolean onlyGmail;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MailAccount {
        private String provider;
        private String host;
        private int port;
        private String username;
        private String password;
        private Map<String, String> properties;
    }
}