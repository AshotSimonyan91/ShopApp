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
        OrderItem orderItem = new OrderItem();
        orderItem.setId(orderItemDto.getId());
        orderItem.setProduct(ProductMapper.map(orderItemDto.getProduct()));
        orderItem.setCount(orderItemDto.getCount());
        return orderItem;
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
