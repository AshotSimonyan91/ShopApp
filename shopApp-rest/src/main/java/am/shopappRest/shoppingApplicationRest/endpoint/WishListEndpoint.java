package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.wishlistDto.WishlistDto;
import am.shoppingCommon.shoppingApplication.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Ashot Simonyan on 27.06.23.
 */

/**
 * RestController class responsible for handling wishlist-related API operations.
 * It provides endpoints to get the user's wishlist, add a product to the wishlist, and remove a product from the wishlist.
 */
@RestController
@RequestMapping("/wishList")
@RequiredArgsConstructor
public class WishListEndpoint {

    private final WishListService wishListService;


    /**
     * Retrieves the user's wishlist based on the currently authenticated user.
     *
     * @param currentUser The information of the currently authenticated user.
     * @return ResponseEntity with the WishlistDto representing the user's wishlist as a JSON response.
     */
    @GetMapping
    public ResponseEntity<WishlistDto> getUserWishlist(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(wishListService.findByUserId(currentUser.getUser().getId()));
    }


    /**
     * Adds a product to the user's wishlist based on the provided product ID.
     *
     * @param productId   The ID of the product to be added to the wishlist.
     * @param currentUser The information of the currently authenticated user.
     * @return ResponseEntity with the updated WishlistDto after adding the product to the wishlist as a JSON response.
     */
    @PostMapping("/add")
    public ResponseEntity<WishlistDto> addWishList(@RequestParam("productId") int productId,
                                                   @AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(wishListService.save(productId, currentUser.getUser()));
    }

    /**
     * Removes a product from the user's wishlist based on the provided wishlist item ID.
     *
     * @param id          The ID of the wishlist item representing the product in the wishlist.
     * @param currentUser The information of the currently authenticated user.
     * @return ResponseEntity with no content (204 No Content) if the product is successfully removed from the wishlist.
     * If the wishlist item with the given ID does not exist, it also returns 204 No Content.
     */
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeWishList(@RequestParam("id") int id,
                                            @AuthenticationPrincipal CurrentUser currentUser) {
        wishListService.remove(id, currentUser.getUser());
        return ResponseEntity.noContent().build();
    }
}
