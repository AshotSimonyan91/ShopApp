package am.shoppingCommon.shoppingApplication.mapper;


import am.shoppingCommon.shoppingApplication.dto.notificationDto.NotificationDto;
import am.shoppingCommon.shoppingApplication.dto.notificationDto.NotificationRequestDto;
import am.shoppingCommon.shoppingApplication.dto.notificationDto.NotificationResponseDto;
import am.shoppingCommon.shoppingApplication.entity.Notification;

import java.util.List;

public class NotificationMapper {
    public static Notification map(NotificationRequestDto notificationRequestDto) {
        if (notificationRequestDto == null) {
            return null;
        }
        return Notification.builder()
                .message(notificationRequestDto.getMessage())
                .subject(notificationRequestDto.getSubject())
                .build();
    }

    public static NotificationDto mapToDto(Notification notification) {
        if (notification == null) {
            return null;
        }
        return NotificationDto.builder()
                .message(notification.getMessage())
                .subject(notification.getSubject())
                .dateTime(notification.getDateTime())
                .user(UserMapper.userToUserDto(notification.getUser()))
                .id(notification.getId())
                .build();
    }

    public static NotificationResponseDto mapToResponseDto(Notification notification) {
        if (notification == null) {
            return null;
        }
        return NotificationResponseDto.builder()
                .message(notification.getMessage())
                .build();
    }

    public static List<NotificationResponseDto> map(List<Notification> notifications) {
        if (notifications == null) {
            return null;
        }
        return notifications.stream()
                .map(NotificationMapper::mapToResponseDto)
                .toList();
    }

    public static List<NotificationDto> mapToListNotificationDto(List<Notification> notifications) {
        if (notifications == null) {
            return null;
        }
        return notifications.stream()
                .map(NotificationMapper::mapToDto)
                .toList();
    }
}
