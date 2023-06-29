package am.shoppingCommon.shoppingApplication.service;

import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.entity.Order;
import am.shoppingCommon.shoppingApplication.entity.User;

public interface AdminService {

    void block(int id, User user);
    void unBlock(int id, User user);

    Order editOrder(OrderDto orderDto, User user);
}
