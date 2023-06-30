package am.shoppingCommon.shoppingApplication.service;

import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.entity.Order;
import am.shoppingCommon.shoppingApplication.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AdminService {

    void block(int id, User user);

    void unBlock(int id, User user);

    Order editOrder(OrderDto orderDto, int deliveryId, User user);

    void updateUser(UserDto userDto, MultipartFile multipartFile) throws IOException;
}
