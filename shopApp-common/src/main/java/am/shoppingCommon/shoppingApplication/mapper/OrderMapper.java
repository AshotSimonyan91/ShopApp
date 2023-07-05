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
        List<OrderItemDto> orderItems = orderDto.getOrderItems();
        List<OrderItem> orderItemsNew = new ArrayList<>();
        for (OrderItemDto orderItem : orderItems) {
            orderItemsNew.add(OrderItemMapper.orderItemDtoToOrderItem(orderItem));
        }
        return Order.builder()
                .dateTime(orderDto.getDateTime())
                .totalAmount(orderDto.getTotalAmount())
                .status(orderDto.getStatus())
                .user(UserMapper.userDtoToUser(orderDto.getUser()))
                .orderItems(orderItemsNew)
                .build();
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
                .user(UserMapper.userToUserDto(order.getUser()))
                .orderItems(orderItemDtos)
                .build();
    }

    public static OrderResponseDto orderToOrderResponseDto(Order order) {
        if (order == null) {
            return null;
        }
        List<OrderItem> orderItems = order.getOrderItems();
        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            orderItemDtos.add(OrderItemMapper.orderItemToOrderItemDto(orderItem));
        }
        return OrderResponseDto
                .builder()
                .dateTime(order.getDateTime())
                .totalAmount(order.getTotalAmount())
                .orderItems(orderItemDtos)
                .build();
    }

    public static List<OrderDto> listOrderToListOrderDto(List<Order> orders) {
        if (orders == null) {
            return null;
        }
        return orders.stream()
                .map(OrderMapper::orderToOrderDto)
                .toList();
    }

    public static List<OrderResponseDto> findAll(List<Order> all) {
        if (all == null) {
            return null;
        }
        return all.stream()
                .map(OrderMapper::orderToOrderResponseDto)
                .toList();

    }
}
