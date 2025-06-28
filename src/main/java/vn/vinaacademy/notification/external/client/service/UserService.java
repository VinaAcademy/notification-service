package vn.vinaacademy.notification.external.client.service;

import vn.vinaacademy.notification.external.client.dto.UserDto;

import java.util.UUID;

public interface UserService {

    UserDto getUserById(UUID userId);
}
