package am.shopappRest.shoppingApplicationRest.cartIntegrationTest;

import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.entity.*;
import am.shoppingCommon.shoppingApplication.repository.CartItemRepository;
import am.shoppingCommon.shoppingApplication.repository.CartRepository;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import am.shoppingCommon.shoppingApplication.service.CartService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CartRemoveTest {
    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void setUp() {
        cartRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

    }

    @Test
    public void testRemoveFromCart() throws Exception {

        User user = createUser("user@shopApp.com", "name", "surname");

        Product product = new Product(0, "Product Name", "pCode", "brand", 0L, "desc", 10.0, 40, user, null, null);
        Product save = productRepository.save(product);

        List<Product> all = productRepository.findAll();
        cartService.save(all.get(0).getId(), user);

        List<CartItem> cartItems = cartItemRepository.findAll();
        assertFalse(cartItems.isEmpty());

        CurrentUser currentUser = new CurrentUser(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(delete("/cart/remove")
                        .param("countRemove", String.valueOf(1))
                        .param("productRemoveId", String.valueOf(save.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        List<CartItem> cartItemsAfterRemove = cartItemRepository.findAll();
        assertTrue(cartItemsAfterRemove.isEmpty());

    }

    private User createUser(String email, String name, String surname) {
        return userRepository.save(User.builder()
                .email(email)
                .name(name)
                .surname(surname)
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .gender(Gender.FEMALE)
                .addresses(null)
                .build());
    }

}
