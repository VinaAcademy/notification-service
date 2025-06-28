package vn.vinaacademy.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import vn.vinaacademy.kafka.KafkaConsumerConfig;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {
        "vn.vinaacademy.notification",
        "vn.vinaacademy.common"
})
@EnableFeignClients
public class NotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args);
    }

}
