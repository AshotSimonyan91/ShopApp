package am.shoppingCommon.shoppingApplication.dto.deliveryDto;

import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.entity.Order;
import am.shoppingCommon.shoppingApplication.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Ashot Simonyan on 04.07.23.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryDto {

    private int id;
    private OrderDto order;
    private UserDto user;
}

