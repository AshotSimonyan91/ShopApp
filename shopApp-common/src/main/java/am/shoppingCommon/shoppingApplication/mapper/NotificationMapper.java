package am.shoppingCommon.shoppingApplication.mapper;



import am.shoppingCommon.shoppingApplication.dto.notificationDto.NotificationRequestDto;
import am.shoppingCommon.shoppingApplication.dto.notificationDto.NotificationResponseDto;
import am.shoppingCommon.shoppingApplication.entity.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationMapper {
    public static Notification map(NotificationRequestDto notificationRequestDto) {
        Notification notification = new Notification();
        notification.setMessage(notificationRequestDto.getMessage());
        notification.setSubject(notificationRequestDto.getSubject());
        return notification;
    }

    public static List<NotificationResponseDto> map(List<Notification> notifications) {
        List<NotificationResponseDto> notificationResponseDtoList = new ArrayList<>();
        NotificationResponseDto notificationResponseDto = new NotificationResponseDto();
        for (Notification notification : notifications) {
            notificationResponseDto.setMessage(notification.getMessage());
            notificationResponseDtoList.add(notificationResponseDto);
        }
        return notificationResponseDtoList;
    }

}
