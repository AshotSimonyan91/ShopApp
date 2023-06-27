package am.shopappRest.shoppingApplicationRest.restDto.orderRequestDto;

import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPageDto {
     private UserDto userDto;
     private OrderDto orderDto;
}
