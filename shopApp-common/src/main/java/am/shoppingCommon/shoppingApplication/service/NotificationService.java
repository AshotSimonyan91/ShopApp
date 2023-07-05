package am.shoppingCommon.shoppingApplication.service;


import am.shoppingCommon.shoppingApplication.dto.notificationDto.NotificationDto;
import am.shoppingCommon.shoppingApplication.dto.notificationDto.NotificationRequestDto;

import java.util.List;

public interface NotificationService {

    List<NotificationDto> findAllByUserId(int id);

    void remove(int id);

    NotificationDto save(NotificationRequestDto notificationRequestDto);

    List<NotificationDto> notifications(int userId);
    List<NotificationDto> last3Notifications(int userId);


}
