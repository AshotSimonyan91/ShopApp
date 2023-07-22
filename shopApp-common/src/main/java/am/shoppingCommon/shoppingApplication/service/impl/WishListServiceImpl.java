package am.shoppingCommon.shoppingApplication.service.impl;


import am.shoppingCommon.shoppingApplication.dto.wishlistDto.WishlistDto;
import am.shoppingCommon.shoppingApplication.mapper.WishListMapper;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import am.shoppingCommon.shoppingApplication.service.WishListService;
import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.entity.WishList;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import am.shoppingCommon.shoppingApplication.repository.WishListRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class WishListServiceImpl implements WishListService {

    private final WishListRepository wishListRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public List<WishlistDto> findAll() {
        List<WishList> all = wishListRepository.findAll();
        return WishListMapper.mapToListDto(all);
    }

    @Override
    public WishlistDto findByUserId(int id) {
        Optional<WishList> byUserId = wishListRepository.findByUserId(id);
        return byUserId.map(WishListMapper::mapToDto).orElse(null);
    }

    @Override
    public void remove(int id) {
        wishListRepository.deleteById(id);
        log.info("wishlist is deleted by ID: {}", id);
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
                log.info("from wishlist is deleted product by ID: {} from user :{}", id, user.getId());
            }
        }

    }

    @Override
    public WishlistDto findByProduct(Product product) {
        Optional<WishList> byProduct = wishListRepository.findByProduct(product);
        return byProduct.map(WishListMapper::mapToDto).orElse(null);
    }

    @Override
    public WishlistDto save(int productId, User user) {
        Optional<Product> byId = productRepository.findById(productId);
        if (byId.isPresent()) {
            Optional<WishList> byUserId = wishListRepository.findByUserId(user.getId());
            if (byUserId.isEmpty()) {
                Set<Product> products = new HashSet<>();
                products.add(byId.get());
                WishList wishList = new WishList();
                Optional<User> userOptional = userRepository.findById(user.getId());
                wishList.setUser(userOptional.orElse(null));
                wishList.setProduct(products);
                WishList save = wishListRepository.save(wishList);
                log.info("wishlist is created by wishListID: {}", save.getId());
                return WishListMapper.mapToDto(save);

            } else {
                WishList wishList = byUserId.get();
                Set<Product> productset = wishList.getProduct();
                productset.add(byId.get());
                wishList.setProduct(productset);
                WishList save = wishListRepository.save(wishList);
                log.info("wishlist is updated by wishListID: {}", save.getId());
                return WishListMapper.mapToDto(save);
            }
        }
        return null;
    }
}
