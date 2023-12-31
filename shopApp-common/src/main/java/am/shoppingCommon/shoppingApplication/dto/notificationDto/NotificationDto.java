package am.shoppingCommon.shoppingApplication.dto.notificationDto;


import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDto {

    private int id;
    private String message;
    private String subject;
    private LocalDateTime dateTime;
    private UserDto user;
}
