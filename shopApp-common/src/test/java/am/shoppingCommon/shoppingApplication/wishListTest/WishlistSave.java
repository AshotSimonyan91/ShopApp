package am.shoppingCommon.shoppingApplication.wishListTest;

import am.shoppingCommon.shoppingApplication.dto.wishlistDto.WishlistDto;
import am.shoppingCommon.shoppingApplication.entity.Address;
import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.entity.WishList;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import am.shoppingCommon.shoppingApplication.repository.WishListRepository;
import am.shoppingCommon.shoppingApplication.service.impl.WishListServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistSave {
    private WishListServiceImpl wishlistService;
    private ProductRepository productRepository;
    private WishListRepository wishListRepository;
    private UserRepository userRepository;
    private Set<Product> productSet = new HashSet<>();


    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        wishListRepository = mock(WishListRepository.class);
        userRepository = mock(UserRepository.class);
        wishlistService = new WishListServiceImpl(wishListRepository, productRepository, userRepository);
    }

    @Test
    void testSave_AddToNewWishlist() {
        int productId = 1;
        int userId = 42;
        User user = new User();
        user.setId(42);
        Address address = new Address();
        List<Address> addressList = new ArrayList<>();
        addressList.add(address);
        user.setAddresses(addressList);
        String test = "test";
        Product product = new Product(1,test,test,test,1L,test,100,100,null,null,null);
        WishList emptyWishlist = new WishList();
        emptyWishlist.setUser(user);
        Set<Product> products = new HashSet<>();
        products.add(product);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(wishListRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(wishListRepository.save(any(WishList.class))).thenReturn(emptyWishlist);

        WishlistDto result = wishlistService.save(1, user);

        assertNotNull(result);
    }


    @Test
    void testSave_ProductNotFound() {
        int productId = 1;
        int userId = 1;
        User user = new User();
        user.setId(userId);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        WishlistDto result = wishlistService.save(productId, user);

        assertNull(result);
    }
}