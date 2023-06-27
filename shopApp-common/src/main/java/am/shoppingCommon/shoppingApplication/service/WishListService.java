package am.shoppingCommon.shoppingApplication.service;


import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.entity.WishList;
import am.shoppingCommon.shoppingApplication.security.CurrentUser;

import java.util.List;
import java.util.Optional;

public interface WishListService {

    List<WishList> findAll();
    WishList findByUserId(int id);

    void remove(int id);

    void remove(int id, CurrentUser currentUser);

    void save(WishList wishList);

    Optional<WishList> findByProduct(Product product);

    void save(int productId, CurrentUser currentUser);
}
