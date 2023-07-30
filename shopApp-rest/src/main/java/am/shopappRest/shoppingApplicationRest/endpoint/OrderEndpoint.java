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

/**
 * RestController class responsible for handling order-related API operations.
 * It provides endpoints to get orders, add a new order, and remove a product from an order.
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderEndpoint {

    private final OrderService orderService;

    /**
     * Retrieves orders for the currently authenticated user with the status "PENDING".
     *
     * @param currentUser The information of the currently authenticated user.
     * @return ResponseEntity with the OrderDto representing the orders as a JSON response.
     */
    @GetMapping()
    public ResponseEntity<OrderDto> getOrders(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(orderService.findByUserIdAndStatus(currentUser.getUser().getId(), Status.PENDING));
    }


    /**
     * Adds a new order for the currently authenticated user.
     *
     * @param currentUser The information of the currently authenticated user.
     * @return ResponseEntity with the list of OrderDto representing all orders of the user as a JSON response.
     */
    @PostMapping("/add")
    public ResponseEntity<List<OrderDto>> addOrder(@AuthenticationPrincipal CurrentUser currentUser) {
        orderService.save(currentUser.getUser().getId());
        return ResponseEntity.ok(orderService.findAllByUserId(currentUser.getUser().getId()));
    }


    /**
     * Removes a product from a user's order based on the provided product ID and order item ID.
     *
     * @param product_id   The ID of the product to be removed from the order.
     * @param orderItem_id The ID of the order item representing the product in the order.
     * @param currentUser  The information of the currently authenticated user.
     * @return ResponseEntity with no content (204 No Content) if the product is successfully removed from the order.
     * If the product or order item with the given IDs does not exist, it also returns 204 No Content.
     */
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeProductFromOrder(@RequestParam("product_id") int product_id,
                                                    @RequestParam("orderItem_id") int orderItem_id,
                                                    @AuthenticationPrincipal CurrentUser currentUser) {
        orderService.removeByProductIdAndOrderItemId(product_id, orderItem_id, currentUser.getUser().getId());
        return ResponseEntity.noContent().build();
    }
}
