package am.shoppingCommon.shoppingApplication.service;



import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderResponseDto;
import am.shoppingCommon.shoppingApplication.entity.Status;

import java.util.List;

public interface OrderService {

    List<OrderResponseDto> findAllOrder();

    OrderDto findByUserIdAndStatus(int id, Status status);

    void removeByProductIdAndOrderItemId(int product_id, int orderItem_id,int userId);

    OrderDto save(int userId);

    List<OrderDto> findAllByUserId(int id);

    List<OrderDto> ordersLimit10();

    OrderDto orderById(int id);
}
