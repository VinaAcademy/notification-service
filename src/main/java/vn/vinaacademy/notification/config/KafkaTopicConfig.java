package vn.vinaacademy.notification.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import vn.vinaacademy.kafka.KafkaTopic;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic createEmailTopic() {
        return TopicBuilder.name(KafkaTopic.EMAIL_TOPIC)
                .build();
    }

    @Bean
    public NewTopic createNotificationTopic() {
        return TopicBuilder.name(KafkaTopic.NOTIFICATION_TOPIC)
                .build();
    }
}