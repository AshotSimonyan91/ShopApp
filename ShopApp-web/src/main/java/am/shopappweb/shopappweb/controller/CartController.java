package am.shopappweb.shopappweb.controller;


import am.shopappweb.shopappweb.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.cartDto.CartDto;
import am.shoppingCommon.shoppingApplication.dto.cartDto.UpdateCartItemRequestDto;
import am.shoppingCommon.shoppingApplication.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * This controller handles cart-related functionalities and serves the corresponding cart-related views.
 * It allows users to view their cart, add products to the cart, remove products from the cart, and update cart item counts.
 */
@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    /**
     * Handles the request to the cart page and populates the model with cart items for rendering the view.
     *
     * @param modelMap    The ModelMap to store attributes to be used in the view.
     * @param currentUser The currently authenticated user.
     * @return The name of the view to be rendered (cart).
     */

    @GetMapping()
    public String cartPage(ModelMap modelMap, @AuthenticationPrincipal CurrentUser currentUser) {
        CartDto allByUserId = cartService.findAllByUser_id(currentUser.getUser().getId());
        if (allByUserId != null && allByUserId.getCartItems() != null) {
            modelMap.addAttribute("cartItems", allByUserId.getCartItems());
        }
        return "cart";
    }

    /**
     * Handles the request to add a product with the specified ID to the user's cart and redirects to the products page.
     *
     * @param id           The ID of the product to be added to the cart.
     * @param currentUser  The currently authenticated user.
     * @return The redirect URL to the products page.
     */

    @GetMapping("/add/{productId}")
    public String saveCart(@PathVariable("productId") int id, @AuthenticationPrincipal CurrentUser currentUser) {
        cartService.save(id, currentUser.getUser());
        return "redirect:/products";
    }

    /**
     * Handles the request to remove a product with the specified ID and the corresponding count from the user's cart.
     *
     * @param count        The count of the product to be removed from the cart.
     * @param currentUser  The currently authenticated user.
     * @param productId    The ID of the product to be removed from the cart.
     * @return The redirect URL to the cart page.
     */
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam("countRemove") int count, @AuthenticationPrincipal CurrentUser currentUser, @RequestParam("productRemoveId") int productId) {
        cartService.remove(currentUser.getUser().getId(), productId, count);
        return "redirect:/cart";
    }

    /**
     * Handles the request to update the counts of multiple cart items in the user's cart.
     * Receives the updated counts as a request body and performs the necessary updates.
     *
     * @param request The UpdateCartItemRequestDto containing the cart item IDs and their updated counts.
     * @return The redirect URL to the cart page.
     */
    @PostMapping("/update")
    public String updateCartItemCounts(@RequestBody UpdateCartItemRequestDto request) {
        List<Integer> counts = request.getCount();
        List<Integer> cartItemIds = request.getCartItem();
        boolean isValid = cartService.updateCartItemCounts(cartItemIds, counts);
        return "redirect:/cart";
    }

}
