package am.shopappRest.shoppingApplicationRest.restDto.deliveryRequestDto;

import am.shoppingCommon.shoppingApplication.dto.deliveryDto.DeliveryDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.entity.Delivery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryRequestDto {
    private int totalPages;
    private int currentPage;
    private List<DeliveryDto> deliveries1;
    private List<DeliveryDto> deliveries2;
    private UserDto user;

}
