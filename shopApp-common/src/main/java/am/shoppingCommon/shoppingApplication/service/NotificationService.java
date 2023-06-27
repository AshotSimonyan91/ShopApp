package am.shoppingCommon.shoppingApplication.service;


import am.shoppingCommon.shoppingApplication.dto.notificationDto.NotificationRequestDto;
import am.shoppingCommon.shoppingApplication.entity.Notification;

import java.util.List;

public interface NotificationService {

    List<Notification> findAllByUserId(int id);

    void remove(int id);

    void save(NotificationRequestDto notificationRequestDto);

    List<Notification> notifications(int userId);
}
