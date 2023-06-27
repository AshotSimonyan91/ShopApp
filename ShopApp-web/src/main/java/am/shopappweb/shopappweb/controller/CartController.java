package am.shopappweb.shopappweb.controller;



import am.shoppingCommon.shoppingApplication.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.service.CartService;
import am.shoppingCommon.shoppingApplication.dto.cartDto.CartDto;
import am.shoppingCommon.shoppingApplication.dto.cartDto.UpdateCartItemRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping()
    public String cartPage(ModelMap modelMap, @AuthenticationPrincipal CurrentUser currentUser) {
        CartDto allByUserId = cartService.findAllByUser_id(currentUser.getUser().getId());
        if (allByUserId != null && allByUserId.getCartItems() != null) {
            modelMap.addAttribute("cartItems", allByUserId.getCartItems());
        }
        return "cart";
    }

    @GetMapping("/add/{productId}")
    public String saveCart(@PathVariable("productId") int id, @AuthenticationPrincipal CurrentUser currentUser) {
        cartService.save(id, currentUser);
        return "redirect:/products";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam("countRemove") int count, @AuthenticationPrincipal CurrentUser currentUser, @RequestParam("productRemoveId") int productId) {
        cartService.remove(currentUser.getUser().getId(), productId, count);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCartItemCounts(@RequestBody UpdateCartItemRequestDto request) {
        List<Integer> counts = request.getCount();
        List<Integer> cartItemIds = request.getCartItem();
        boolean isValid = cartService.updateCartItemCounts(cartItemIds, counts);
        return "redirect:/cart";
    }

}
