package am.shoppingCommon.shoppingApplication.mapper;


import am.shoppingCommon.shoppingApplication.dto.cartDto.CartDto;
import am.shoppingCommon.shoppingApplication.dto.cartDto.CartItemDto;
import am.shoppingCommon.shoppingApplication.entity.Cart;
import am.shoppingCommon.shoppingApplication.entity.CartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CartMapper {
    public static CartDto convertToDto(Cart cart) {
        if (cart == null) {
            return null;
        }
        List<CartItemDto> cartItemDTOs = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            CartItemDto build = CartItemDto.builder()
                    .id(cartItem.getId())
                    .count(cartItem.getCount())
                    .product(ProductMapper.mapToDto(cartItem.getProduct()))
                    .build();
            cartItemDTOs.add(build);
        }
        return CartDto.builder()
                .userId(cart.getUser().getId())
                .cartItems(cartItemDTOs)
                .build();
    }

    public static List<CartDto> findAllByUser_id(List<Cart> allByUserId) {
        if (allByUserId == null) {
            return null;
        }
        return allByUserId.stream().
                map(CartMapper::convertToDto)
                .toList();
    }

    public static List<CartItemDto> mapToDtoList(List<CartItem> cartItems) {
        if (cartItems == null) {
            return null;
        }
        return cartItems.stream()
                .map(CartMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public static CartItemDto mapToDto(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }
        return CartItemDto.builder()
                .id(cartItem.getId())
                .count(cartItem.getCount())
                .product(ProductMapper.mapToDto(cartItem.getProduct()))
                .build();
    }
}
