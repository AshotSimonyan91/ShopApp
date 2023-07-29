package am.shoppingCommon.shoppingApplication.service.impl;

import am.shoppingCommon.shoppingApplication.entity.CartItem;
import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.repository.CartItemRepository;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceImplTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void updateCartItemCounts() {
        int cartItemId1 = 1;
        int cartItemId2 = 2;
        List<Integer> cartItemIds = Arrays.asList(cartItemId1, cartItemId2);
        List<Integer> counts = Arrays.asList(3, 5);

        CartItem cartItem1 = new CartItem();
        cartItem1.setId(cartItemId1);
        cartItem1.setCount(2);

        CartItem cartItem2 = new CartItem();
        cartItem2.setId(cartItemId2);
        cartItem2.setCount(4);

        Product product1 = new Product();
        product1.setId(101);
        product1.setCount(10);

        Product product2 = new Product();
        product2.setId(102);
        product2.setCount(8);

        cartItem1.setProduct(product1);

        cartItem2.setProduct(product2);

        when(cartItemRepository.findById(cartItemId1)).thenReturn(Optional.of(cartItem1));
        when(cartItemRepository.findById(cartItemId2)).thenReturn(Optional.of(cartItem2));
        when(productRepository.findById(101)).thenReturn(Optional.of(product1));
        when(productRepository.findById(102)).thenReturn(Optional.of(product2));

        boolean result = cartService.updateCartItemCounts(cartItemIds, counts);

        assertTrue(result);
        assertEquals(3, cartItem1.getCount());
        assertEquals(5, cartItem2.getCount());
        assertEquals(9, product1.getCount());
        assertEquals(7, product2.getCount());

        verify(cartItemRepository, times(2)).findById(anyInt());
        verify(productRepository, times(2)).findById(anyInt());
    }
}