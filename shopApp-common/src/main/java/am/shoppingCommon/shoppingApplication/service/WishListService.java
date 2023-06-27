package am.shoppingCommon.shoppingApplication.service;


import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.entity.WishList;

import java.util.List;
import java.util.Optional;

public interface WishListService {

    List<WishList> findAll();
    WishList findByUserId(int id);

    void remove(int id);

    void remove(int id, User user);

    void save(WishList wishList);

    Optional<WishList> findByProduct(Product product);

    void save(int productId, User user);
}
