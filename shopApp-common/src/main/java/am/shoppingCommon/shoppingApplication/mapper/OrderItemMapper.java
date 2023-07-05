package am.shoppingCommon.shoppingApplication.mapper;


import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderItemDto;
import am.shoppingCommon.shoppingApplication.entity.OrderItem;

/**
 * Created by Ashot Simonyan on 10.06.23.
 */

public class OrderItemMapper {

    public static OrderItem orderItemDtoToOrderItem(OrderItemDto orderItemDto) {
        if (orderItemDto == null) {
            return null;
        }
        return OrderItem.builder()
                .id(orderItemDto.getId())
                .product(ProductMapper.map(orderItemDto.getProduct()))
                .count(orderItemDto.getCount())
                .build();
    }

    public static OrderItemDto orderItemToOrderItemDto(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        return OrderItemDto.builder()
                .id(orderItem.getId())
                .count(orderItem.getCount())
                .product(ProductMapper.mapToResponseDto(orderItem.getProduct()))
                .build();
    }
}
