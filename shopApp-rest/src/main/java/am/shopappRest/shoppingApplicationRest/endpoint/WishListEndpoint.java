package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shoppingCommon.shoppingApplication.dto.wishlistDto.WishlistResponseDto;
import am.shoppingCommon.shoppingApplication.mapper.WishListMapper;
import am.shoppingCommon.shoppingApplication.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Ashot Simonyan on 27.06.23.
 */

@RestController
@RequestMapping("/wishList")
@RequiredArgsConstructor
public class WishListEndpoint {

    private final WishListService wishListService;

    @GetMapping
    public ResponseEntity<WishlistResponseDto> wishListPage(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(WishListMapper.map(wishListService.findByUserId(currentUser.getUser().getId())));
    }

    @GetMapping("/add")
    public ResponseEntity<?> addWishList(@RequestParam("productId") int productId,
                                         @AuthenticationPrincipal CurrentUser currentUser) {
        wishListService.save(productId, currentUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/remove")
    public ResponseEntity<?> removeWishList(@RequestParam("id") int id,
                                            @AuthenticationPrincipal CurrentUser currentUser) {
        wishListService.remove(id, currentUser);
        return ResponseEntity.ok().build();
    }
}
