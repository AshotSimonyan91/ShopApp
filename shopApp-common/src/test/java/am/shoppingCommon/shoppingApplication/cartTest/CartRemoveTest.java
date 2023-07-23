package am.shoppingCommon.shoppingApplication.cartTest;

import am.shoppingCommon.shoppingApplication.entity.Cart;
import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.repository.CartItemRepository;
import am.shoppingCommon.shoppingApplication.repository.CartRepository;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import am.shoppingCommon.shoppingApplication.service.impl.CartServiceImpl;
import am.shoppingCommon.shoppingApplication.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class CartRemoveTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cartService = new CartServiceImpl(cartRepository, productRepository, cartItemRepository);
    }

    @Test
    public void testRemove_ProductExistsInCart_Success() {
        int cartId = 1;
        int productId = 100;
        int count = 2;

        Cart cart = new Cart();
        cart.setId(cartId);

        Product product = new Product();
        product.setId(productId);
        product.setCount(5);

        when(cartRepository.findAllByUser_Id(cartId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        cartService.remove(cartId, productId, count);

        assertEquals(7, product.getCount());
        verify(cartItemRepository).deleteByCart_IdAndProduct_Id(cart.getId(), productId);
    }

}

