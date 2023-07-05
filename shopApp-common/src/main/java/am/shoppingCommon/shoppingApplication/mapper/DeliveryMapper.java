package am.shoppingCommon.shoppingApplication.mapper;

import am.shoppingCommon.shoppingApplication.dto.deliveryDto.DeliveryDto;
import am.shoppingCommon.shoppingApplication.entity.Delivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

/**
 * Created by Ashot Simonyan on 04.07.23.
 */

public class DeliveryMapper {

    public static Page<DeliveryDto> mapPageToDto(Page<Delivery> deliveryPage) {
        if (deliveryPage == null) {
            return null;
        }
        List<DeliveryDto> deliveryDtoList = deliveryPage.getContent()
                .stream()
                .map(DeliveryMapper::mapToDto)
                .toList();

        return new PageImpl<>(deliveryDtoList, deliveryPage.getPageable(), deliveryPage.getTotalElements());
    }

    public static DeliveryDto mapToDto(Delivery delivery) {
        if (delivery == null) {
            return null;
        }
        return DeliveryDto.builder()
                .id(delivery.getId())
                .order(OrderMapper.orderToOrderDto(delivery.getOrder()))
                .user(UserMapper.userToUserDto(delivery.getUser()))
                .build();
    }
}