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

/**
 * This endpoint class provides REST API endpoints for handling shopping cart operations in the shopping application.
 * It allows users to view their cart items, add products to the cart, remove products from the cart, and update
 * the quantities of items in the cart. The class is secured using Spring Security's @AuthenticationPrincipal to get
 * the current authenticated user. The endpoints return ResponseEntity objects with appropriate response data.
 */

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartEndpoint {
    private final CartService cartService;

    /**
     * GET endpoint that retrieves the user's shopping cart page data. It returns a list of CartItemDto objects representing
     * the items in the user's cart, based on the current authenticated user.
     *
     * @param currentUser The CurrentUser object representing the currently authenticated user.
     * @return ResponseEntity containing the list of CartItemDto objects with cart item information.
     */
    @GetMapping
    public ResponseEntity<List<CartItemDto>> cartPage(@AuthenticationPrincipal CurrentUser currentUser) {
        CartDto allByUserId = cartService.findAllByUser_id(currentUser.getUser().getId());
        if (allByUserId != null && allByUserId.getCartItems() != null) {
            return ResponseEntity.ok(allByUserId.getCartItems());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * GET endpoint that handles adding a product to the user's shopping cart. It returns a CartDto object representing
     * the updated cart after adding the product with the specified ID.
     *
     * @param id          The ID of the product to be added to the cart.
     * @param currentUser The CurrentUser object representing the currently authenticated user.
     * @return ResponseEntity containing the CartDto object with updated cart information.
     */
    @GetMapping("/add/{productId}")
    public ResponseEntity<CartDto> saveCart(@PathVariable("productId") int id, @AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(cartService.save(id, currentUser.getUser()));
    }

    /**
     * DELETE endpoint that handles removing a product from the user's shopping cart. It removes the specified quantity
     * of the product with the given ID from the cart of the current authenticated user.
     *
     * @param count       The quantity of the product to be removed from the cart.
     * @param currentUser The CurrentUser object representing the currently authenticated user.
     * @param productId   The ID of the product to be removed from the cart.
     * @return ResponseEntity with no content to indicate successful removal.
     */
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(@RequestParam("countRemove") int count, @AuthenticationPrincipal CurrentUser currentUser, @RequestParam("productRemoveId") int productId) {
        cartService.remove(currentUser.getUser().getId(), productId, count);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT endpoint that handles updating the quantities of items in the user's shopping cart. It updates the quantities
     * of cart items based on the provided UpdateCartItemRequestDto, which contains the cart item IDs and their new counts.
     * It returns a ResponseEntity with a boolean value indicating whether the cart item counts were updated successfully.
     *
     * @param request The UpdateCartItemRequestDto containing cart item IDs and their new counts for update.
     * @return ResponseEntity containing a boolean value indicating the success of cart item count updates.
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateCartItemCounts(@RequestBody UpdateCartItemRequestDto request) {
        List<Integer> counts = request.getCount();
        List<Integer> cartItemIds = request.getCartItem();
        return ResponseEntity.ok(cartService.updateCartItemCounts(cartItemIds, counts));
    }
}
