package am.shoppingCommon.shoppingApplication.mapper;


import am.shoppingCommon.shoppingApplication.dto.wishlistDto.WishlistDto;
import am.shoppingCommon.shoppingApplication.dto.wishlistDto.WishlistResponseDto;
import am.shoppingCommon.shoppingApplication.entity.WishList;

import java.util.List;
import java.util.stream.Collectors;

public class WishListMapper {
    public static WishlistResponseDto map(WishList wishList) {
        if (wishList == null) {
            return null;
        }
        return WishlistResponseDto.builder()
                .product(ProductMapper.mapToDto(wishList.getProduct()))
                .build();
    }

    public static WishlistDto mapToDto(WishList wishList) {
        if (wishList == null) {
            return null;
        }
        return WishlistDto.builder()
                .id(wishList.getId())
                .user(UserMapper.userToUserDto(wishList.getUser()))
                .product(wishList.getProduct().stream()
                        .map(ProductMapper::mapToDto)
                        .collect(Collectors.toSet())
                )
                .build();
    }

    public static List<WishlistDto> mapToListDto(List<WishList> wishLists) {
        if (wishLists == null) {
            return null;
        }
        return wishLists.stream()
                .map(WishListMapper::mapToDto)
                .toList();
    }
}