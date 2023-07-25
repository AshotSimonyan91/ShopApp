package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.entity.Status;
import am.shoppingCommon.shoppingApplication.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Ashot Simonyan on 27.06.23.
 */

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderEndpoint {

    private final OrderService orderService;

    @GetMapping()
    public ResponseEntity<OrderDto> getOrders(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(orderService.findByUserIdAndStatus(currentUser.getUser().getId(), Status.PENDING));
    }

    @PostMapping("/add")
    public ResponseEntity<List<OrderDto>> addOrder(@AuthenticationPrincipal CurrentUser currentUser) {
        orderService.save(currentUser.getUser().getId());
        return ResponseEntity.ok(orderService.findAllByUserId(currentUser.getUser().getId()));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeProductFromOrder(@RequestParam("product_id") int product_id,
                                                    @RequestParam("orderItem_id") int orderItem_id,
                                                    @AuthenticationPrincipal CurrentUser currentUser) {
        orderService.removeByProductIdAndOrderItemId(product_id, orderItem_id, currentUser.getUser().getId());
        return ResponseEntity.noContent().build();
    }
}
