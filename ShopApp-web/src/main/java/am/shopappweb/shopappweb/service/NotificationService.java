package am.shopappweb.shopappweb.service;


import am.shoppingCommon.shoppingApplication.dto.notificationDto.NotificationRequestDto;
import am.shoppingCommon.shoppingApplication.dto.notificationDto.NotificationResponseDto;

import java.util.List;

public interface NotificationService {

    List<NotificationResponseDto> findAllByUserId(int id);

    void remove(int id);

    void save(NotificationRequestDto notificationRequestDto);
}
