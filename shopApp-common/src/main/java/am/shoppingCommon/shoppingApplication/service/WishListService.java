package am.shoppingCommon.shoppingApplication.service;


import am.shoppingCommon.shoppingApplication.dto.wishlistDto.WishlistDto;
import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.entity.User;

import java.util.List;

public interface WishListService {

    List<WishlistDto> findAll();
    WishlistDto findByUserId(int id);

    void remove(int id);

    void remove(int id, User user);

    WishlistDto findByProduct(Product product);

    WishlistDto save(int productId, User user);
}
