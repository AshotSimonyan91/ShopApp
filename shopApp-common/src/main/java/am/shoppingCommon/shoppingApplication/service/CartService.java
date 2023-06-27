package am.shoppingCommon.shoppingApplication.service;


import am.shoppingCommon.shoppingApplication.dto.cartDto.CartDto;
import am.shoppingCommon.shoppingApplication.dto.cartDto.CartItemDto;
import am.shoppingCommon.shoppingApplication.security.CurrentUser;

import java.util.List;

public interface CartService {

    List<CartItemDto> findLastCartItemsByLimit(int userId);

    CartDto findAllByUser_id(int id);

    void save(int id, CurrentUser currentUser);

    void remove(int id, int productId, int count);

    boolean updateCartItemCounts(List<Integer> cartItemIds, List<Integer> counts);
}