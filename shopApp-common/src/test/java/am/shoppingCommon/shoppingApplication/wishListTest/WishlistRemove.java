package am.shoppingCommon.shoppingApplication.wishListTest;

import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.entity.WishList;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import am.shoppingCommon.shoppingApplication.repository.WishListRepository;
import am.shoppingCommon.shoppingApplication.service.impl.WishListServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistRemove {

    @InjectMocks
    private WishListServiceImpl wishlistRemove;
    @Mock
    private WishListRepository wishListRepository;

    @Mock
    private ProductRepository productRepository;

    @Test
    void testRemoveProductFromWishList() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1);

        Product product = mock(Product.class);
        when(product.getId()).thenReturn(1);

        WishList wishList = mock(WishList.class);
        Set<Product> products = new HashSet<>();
        products.add(product);
        when(wishList.getProduct()).thenReturn(products);

        when(wishListRepository.findByUserId(user.getId())).thenReturn(Optional.of(wishList));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        wishlistRemove.remove(product.getId(), user);

        verify(wishList, times(1)).getProduct();
        verify(wishList, times(1)).setProduct(any());
        verify(wishListRepository, times(1)).save(wishList);

    }

    @Test
    void testRemoveProductFromWishList_UserNotFound() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1);
        when(wishListRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
        wishlistRemove.remove(1, user);
    }

}