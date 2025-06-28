package vn.vinaacademy.notification.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vn.vinaacademy.kafka.event.NotificationCreateEvent;
import vn.vinaacademy.notification.dto.NotificationCreateDTO;
import vn.vinaacademy.notification.dto.NotificationDTO;
import vn.vinaacademy.notification.entity.Notification;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

//    @Mapping(target = "email", source = "user.email")
    NotificationDTO toDTO(Notification notification);

    NotificationCreateDTO toDTO(NotificationCreateEvent createEvent);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isRead", ignore = true)
    @Mapping(target = "readAt", ignore = true)
    Notification toEntity(NotificationCreateDTO dto);
}