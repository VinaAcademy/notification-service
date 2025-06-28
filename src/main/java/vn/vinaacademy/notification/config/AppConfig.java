package vn.vinaacademy.notification.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import vn.vinaacademy.kafka.KafkaConsumerConfig;

@Configuration
@EnableConfigurationProperties(MailProperties.class)
@Import(KafkaConsumerConfig.class)
public class AppConfig {
}
