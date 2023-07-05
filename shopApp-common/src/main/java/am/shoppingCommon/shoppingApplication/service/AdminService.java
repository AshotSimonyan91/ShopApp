package am.shoppingCommon.shoppingApplication.service;

import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AdminService {

    UserDto block(int id, User user);

    UserDto unBlock(int id, User user);

    OrderDto editOrder(OrderDto orderDto, int deliveryId, User user);

    UserDto updateUser(UserDto userDto, MultipartFile multipartFile) throws IOException;
}
