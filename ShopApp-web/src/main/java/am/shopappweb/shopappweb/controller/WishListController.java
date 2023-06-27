package am.shopappweb.shopappweb.controller;


import am.shoppingCommon.shoppingApplication.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/wishList")
@RequiredArgsConstructor
public class WishListController {
    private final WishListService wishListService;


    @GetMapping
    public String wishListPage(ModelMap modelMap,@AuthenticationPrincipal CurrentUser currentUser) {
        modelMap.addAttribute("wishlistById", wishListService.findByUserId(currentUser.getUser().getId()));
        return "wishlist";
    }


    @GetMapping("/add")
    public String addWishList(@RequestParam("productId") int productId,
                              @AuthenticationPrincipal CurrentUser currentUser) {
        wishListService.save(productId, currentUser);
        return "redirect:/products";
    }

    @GetMapping("/remove")
    public String removeWishList(@RequestParam("id") int id,
                                 @AuthenticationPrincipal CurrentUser currentUser) {
        wishListService.remove(id, currentUser);
        return "redirect:/wishList?userid=" + currentUser.getUser().getId();
    }
}
