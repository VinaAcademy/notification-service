package vn.vinaacademy.notification.external.client.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.vinaacademy.common.exception.BadRequestException;
import vn.vinaacademy.notification.external.client.UserClient;
import vn.vinaacademy.notification.external.client.dto.UserDto;
import vn.vinaacademy.notification.external.client.service.UserService;

import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    public static final String SERVICE_NAME = "userService";

    private final UserClient userClient;

    public UserServiceImpl(UserClient userClient) {
        this.userClient = userClient;
    }

    @Override
    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "getUserByIdFallback")
    @Retry(name = SERVICE_NAME)
    public UserDto getUserById(UUID userId) {
        return userClient.getUserByIdAsDto(userId).getData();
    }

    public UserDto getUserByIdFallback(UUID userId, Throwable throwable) {
        log.warn("Fallback for getUserById called for userId: {} due to: {}", userId, throwable.getMessage());
        if (throwable instanceof BadRequestException) {
            throw (BadRequestException) throwable;
        }
        log.error("Error fetching user by ID: {}. Returning null.", userId, throwable);
        throw new RuntimeException(
                "Failed to fetch user by ID: " + userId + ". Please try again later.", throwable);
    }
}
