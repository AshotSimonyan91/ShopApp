package am.shoppingCommon.shoppingApplication.mapper;



import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderItemDto;
import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderResponseDto;
import am.shoppingCommon.shoppingApplication.entity.Order;
import am.shoppingCommon.shoppingApplication.entity.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderMapper {


    public static Order orderDtoToOrder(OrderDto orderDto) {
        if (orderDto == null) {
            return null;
        }
        Order order = new Order();
        order.setDateTime(orderDto.getDateTime());
        order.setTotalAmount(orderDto.getTotalAmount());
        order.setStatus(orderDto.getStatus());
        order.setUser(orderDto.getUser());
        List<OrderItemDto> orderItems = orderDto.getOrderItems();
        List<OrderItem> orderItems1 = new ArrayList<>();
        for (OrderItemDto orderItem : orderItems) {
            orderItems1.add(OrderItemMapper.orderItemDtoToOrderItem(orderItem));
        }
        order.setOrderItems(orderItems1);
        return order;
    }


    public static List<OrderResponseDto> findAll(List<Order> all) {
        List<OrderResponseDto> orderDtoList = new ArrayList<>();
        for (Order order : all) {
            List<OrderItemDto> orderItemsDto = new ArrayList<>();
            for (OrderItem orderItem : order.getOrderItems()) {
                OrderItemDto orderItemDto = new OrderItemDto();
                orderItemDto.setId(orderItem.getId());
                orderItemDto.setProduct(ProductMapper.mapToResponseDto(orderItem.getProduct()));
                orderItemDto.setCount(orderItem.getCount());
                orderItemsDto.add(orderItemDto);
            }
            orderDtoList.add(OrderResponseDto.builder()
                    .dateTime(order.getDateTime())
                    .totalAmount(order.getTotalAmount())
                    .orderItems(orderItemsDto)
                    .dateTime(order.getDateTime())
                    .build());
        }
        return orderDtoList;
    }

    public static OrderDto orderToOrderDto(Order order) {
        if (order == null) {
            return null;
        }
        List<OrderItem> orderItems = order.getOrderItems();
        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            orderItemDtos.add(OrderItemMapper.orderItemToOrderItemDto(orderItem));
        }
        return OrderDto
                .builder()
                .id(order.getId())
                .dateTime(order.getDateTime())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .user(order.getUser())
                .orderItems(orderItemDtos)
                .build();
    }

    public static List<OrderDto> listOrderToListOrderDto(List<Order> orders) {
        if (orders == null) {
            return null;
        }
        List<OrderDto> list = new ArrayList<>(orders.size());
        for (Order order : orders) {
            list.add(OrderMapper.orderToOrderDto(order));
        }
        return list;
    }
}
