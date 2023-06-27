package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.cartDto.CartDto;
import am.shoppingCommon.shoppingApplication.dto.cartDto.CartItemDto;
import am.shoppingCommon.shoppingApplication.dto.cartDto.UpdateCartItemRequestDto;
import am.shoppingCommon.shoppingApplication.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartEndpoint {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartItemDto>> cartPage(@AuthenticationPrincipal CurrentUser currentUser) {
        CartDto allByUserId = cartService.findAllByUser_id(currentUser.getUser().getId());
        if (allByUserId != null && allByUserId.getCartItems() != null) {
            return ResponseEntity.ok(allByUserId.getCartItems());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/add/{productId}")
    public ResponseEntity<?> saveCart(@PathVariable("productId") int id, @AuthenticationPrincipal CurrentUser currentUser) {
        cartService.save(id, currentUser.getUser());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(@RequestParam("countRemove") int count, @AuthenticationPrincipal CurrentUser currentUser, @RequestParam("productRemoveId") int productId) {
        cartService.remove(currentUser.getUser().getId(), productId, count);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCartItemCounts(@RequestBody UpdateCartItemRequestDto request) {
        List<Integer> counts = request.getCount();
        List<Integer> cartItemIds = request.getCartItem();
        return ResponseEntity.ok(cartService.updateCartItemCounts(cartItemIds, counts));
    }
}
