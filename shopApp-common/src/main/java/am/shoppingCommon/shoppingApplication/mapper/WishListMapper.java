package am.shoppingCommon.shoppingApplication.mapper;


import am.shoppingCommon.shoppingApplication.dto.wishlistDto.WishlistResponseDto;
import am.shoppingCommon.shoppingApplication.entity.WishList;

public class WishListMapper {
    public static WishlistResponseDto map(WishList wishlist) {
        if (wishlist == null) {
            return null;
        }
        WishlistResponseDto wishlistResponseDto = new WishlistResponseDto();
        wishlistResponseDto.setProduct(ProductMapper.mapToDto(wishlist.getProduct()));
        return wishlistResponseDto;
    }
}