package am.shoppingCommon.shoppingApplication.service.impl;


import am.shoppingCommon.shoppingApplication.service.WishListService;
import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.entity.WishList;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import am.shoppingCommon.shoppingApplication.repository.WishListRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Ashot Simonyan on 21.05.23.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class WishListServiceImpl implements WishListService {

    private final WishListRepository wishListRepository;
    private final ProductRepository productRepository;

    @Override
    public List<WishList> findAll() {
        return wishListRepository.findAll();
    }

    @Override
    public WishList findByUserId(int id) {
        return wishListRepository.findByUserId(id).orElse(null);
    }

    @Override
    public void remove(int id) {
        wishListRepository.deleteById(id);
    }

    @Override
    public void remove(int id, User user) {
        Optional<WishList> byUserId = wishListRepository.findByUserId(user.getId());
        if (byUserId.isPresent()) {
            Optional<Product> byId = productRepository.findById(id);
            if (byId.isPresent()) {
                WishList wishList = byUserId.get();
                Set<Product> product = wishList.getProduct();
                product.remove(byId.get());
                wishList.setProduct(product);
                wishListRepository.save(wishList);
            }
        }

    }


    @Override
    public void save(WishList wishList) {
        wishListRepository.save(wishList);
    }

    @Override
    public Optional<WishList> findByProduct(Product product) {
        return wishListRepository.findByProduct(product);
    }

    @Override
    public void save(int productId, User user) {
        Optional<Product> byId = productRepository.findById(productId);
        if (byId.isPresent()) {
            Optional<WishList> byUserId = wishListRepository.findByUserId(user.getId());
            if (byUserId.isEmpty()) {
                Set<Product> products = new HashSet<>();
                products.add(byId.get());
                WishList wishList = new WishList();
                wishList.setUser(user);
                wishList.setProduct(products);
                wishListRepository.save(wishList);
            } else {
                WishList wishList = byUserId.get();
                Set<Product> productset = wishList.getProduct();
                productset.add(byId.get());
                wishList.setProduct(productset);
                wishListRepository.save(wishList);
            }
        }
    }
}
