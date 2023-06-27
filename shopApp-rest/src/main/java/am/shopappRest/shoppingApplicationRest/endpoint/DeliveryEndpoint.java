package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shopappRest.shoppingApplicationRest.restDto.orderRequestDto.OrderPageDto;
import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.entity.Order;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.mapper.OrderMapper;
import am.shoppingCommon.shoppingApplication.mapper.UserMapper;
import am.shoppingCommon.shoppingApplication.service.DeliveryService;
import am.shoppingCommon.shoppingApplication.service.OrderService;
import am.shoppingCommon.shoppingApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class DeliveryEndpoint {
    private final DeliveryService deliveryService;
    private final UserService userService;
    private final OrderService orderService;

    @GetMapping("/add")
    public ResponseEntity<?> addDelivery(@RequestParam("order_id") int id) {
        deliveryService.save(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeDelivery(@RequestParam("id") int id, @RequestParam("ProductId") int productId) {
        deliveryService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer")
    public ResponseEntity<UserDto> deliveryUserPage(ModelMap modelmap,
                                                    @RequestParam("id") int id) {
        User byIdWithAddresses = userService.findByIdWithAddresses(id);
        if (byIdWithAddresses != null) {
            return ResponseEntity.ok(UserMapper.userToUserDto(byIdWithAddresses));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/order")
    public ResponseEntity<OrderPageDto> deliveryOrderPage(ModelMap modelmap,
                                                          @AuthenticationPrincipal CurrentUser currentUser,
                                                          @RequestParam("id") int id) {
        User byIdWithAddresses = userService.findByIdWithAddresses(currentUser.getUser().getId());
        Order order = orderService.findById(id).orElse(null);
        if (order != null) {
            OrderPageDto orderPageDto = new OrderPageDto();
            orderPageDto.setOrderDto(OrderMapper.orderToOrderDto(order));
            orderPageDto.setUserDto(UserMapper.userToUserDto(byIdWithAddresses));
            return ResponseEntity.ok(orderPageDto);
        }
        return ResponseEntity.badRequest().build();
    }
}
