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

@RestController
@RequestMapping("/wishList")
@RequiredArgsConstructor
public class WishListEndpoint {

    private final WishListService wishListService;

    @GetMapping
    public ResponseEntity<WishlistDto> getUserWishlist(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(wishListService.findByUserId(currentUser.getUser().getId()));
    }

    @PostMapping("/add")
    public ResponseEntity<WishlistDto> addWishList(@RequestParam("productId") int productId,
                                         @AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(wishListService.save(productId, currentUser.getUser()));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeWishList(@RequestParam("id") int id,
                                            @AuthenticationPrincipal CurrentUser currentUser) {
        wishListService.remove(id, currentUser.getUser());
        return ResponseEntity.noContent().build();
    }
}
