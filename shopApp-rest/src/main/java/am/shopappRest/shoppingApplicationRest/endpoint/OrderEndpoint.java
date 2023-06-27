package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.entity.Order;
import am.shoppingCommon.shoppingApplication.entity.Status;
import am.shoppingCommon.shoppingApplication.mapper.OrderMapper;
import am.shoppingCommon.shoppingApplication.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Created by Ashot Simonyan on 27.06.23.
 */

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderEndpoint {

    private final OrderService orderService;

    @GetMapping()
    public ResponseEntity<OrderDto> orderPage(@AuthenticationPrincipal CurrentUser currentUser) {
        Optional<Order> byUserIdAndStatus = orderService
                .findByUserIdAndStatus(currentUser.getUser().getId(), Status.PENDING);
        return ResponseEntity.ok(OrderMapper.orderToOrderDto(byUserIdAndStatus.orElse(null)));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addOrder(@AuthenticationPrincipal CurrentUser currentUser) {
        orderService.save(currentUser.getUser().getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/remove")
    public ResponseEntity<?> removeProductFromOrder(@RequestParam("product_id") int product_id,
                                                    @RequestParam("orderItem_id") int orderItem_id,
                                                    @AuthenticationPrincipal CurrentUser currentUser) {
        orderService.removeByProductIdAndOrderItemId(product_id, orderItem_id, currentUser.getUser().getId());
        return ResponseEntity.ok().build();
    }
}
