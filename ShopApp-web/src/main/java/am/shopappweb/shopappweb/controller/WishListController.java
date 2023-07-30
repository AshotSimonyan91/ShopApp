package am.shopappweb.shopappweb.controller;


import am.shopappweb.shopappweb.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller class responsible for handling user's wish list operations.
 * This class provides endpoints to view, add, and remove items from the user's wish list.
 * The endpoints are mapped to "/wishList" and its subpaths.
 */
@Controller
@RequestMapping("/wishList")
@RequiredArgsConstructor
public class WishListController {
    private final WishListService wishListService;

    /**
     * Retrieves the wish list page for the currently authenticated user.
     * It populates the model with the user's wish list items and renders the "wishlist" view.
     *
     * @param modelMap    The model map to hold the wish list data for the view.
     * @param currentUser The currently authenticated user's information.
     * @return The view name "wishlist" to be rendered.
     */
    @GetMapping
    public String wishListPage(ModelMap modelMap, @AuthenticationPrincipal CurrentUser currentUser) {
        modelMap.addAttribute("wishlistById", wishListService.findByUserId(currentUser.getUser().getId()));
        return "wishlist";
    }


    /**
     * Adds a new product to the user's wish list.
     * It takes the product ID and the current authenticated user as parameters.
     *
     * @param productId   The ID of the product to be added to the wish list.
     * @param currentUser The currently authenticated user's information.
     * @return A redirection to the "/products" page after adding the product to the wish list.
     */
    @GetMapping("/add")
    public String addWishList(@RequestParam("productId") int productId,
                              @AuthenticationPrincipal CurrentUser currentUser) {
        wishListService.save(productId, currentUser.getUser());
        return "redirect:/products";
    }

    /**
     * Removes an item from the user's wish list based on the item's ID.
     * It takes the item's ID and the current authenticated user as parameters.
     *
     * @param id          The ID of the wish list item to be removed.
     * @param currentUser The currently authenticated user's information.
     * @return A redirection to the "/wishList" page after removing the item from the wish list.
     */
    @GetMapping("/remove")
    public String removeWishList(@RequestParam("id") int id,
                                 @AuthenticationPrincipal CurrentUser currentUser) {
        wishListService.remove(id, currentUser.getUser());
        return "redirect:/wishList?userid=" + currentUser.getUser().getId();
    }
}
