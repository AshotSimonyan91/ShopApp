package am.shoppingCommon.shoppingApplication.service;



import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderResponseDto;
import am.shoppingCommon.shoppingApplication.entity.Order;
import am.shoppingCommon.shoppingApplication.entity.Status;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    List<OrderResponseDto> findAllOrder();

    Optional<Order> findByUserIdAndStatus(int id, Status status);

    void removeByProductIdAndOrderItemId(int product_id, int orderItem_id,int userId);

    void save(int userId);

    List<Order> findAllByUserId(int id);

    Optional<Order> findById(int id);

    List<OrderDto> ordersLimit10();

    Order orderById(int id);
}
