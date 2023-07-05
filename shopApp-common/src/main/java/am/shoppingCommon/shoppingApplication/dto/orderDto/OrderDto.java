package am.shoppingCommon.shoppingApplication.dto.orderDto;


import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {

    private int id;
    private LocalDateTime dateTime;
    private double totalAmount;
    private Status status;
    private UserDto user;
    private List<OrderItemDto> orderItems;
}
