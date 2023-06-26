package am.shopappweb.shopappweb.service;


import am.shopappweb.shopappweb.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.wishlistDto.WishlistResponseDto;
import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.entity.WishList;

import java.util.List;
import java.util.Optional;

public interface WishListService {

    List<WishList> findAll();
    WishlistResponseDto findByUserId(int id);

    void remove(int id);

    void remove(int id, CurrentUser currentUser);

    void save(WishList wishList);

    Optional<WishList> findByProduct(Product product);

    void save(int productId, CurrentUser currentUser);
}
