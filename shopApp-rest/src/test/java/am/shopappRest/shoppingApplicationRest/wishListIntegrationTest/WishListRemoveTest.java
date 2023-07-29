package am.shopappRest.shoppingApplicationRest.wishListIntegrationTest;

import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.entity.*;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import am.shoppingCommon.shoppingApplication.repository.WishListRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Ashot Simonyan on 29.07.23.
 */

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test2")
public class WishListRemoveTest {

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void before(){
        wishListRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    public void wishListRemove() throws Exception {
		Product product = createProduct(33);

        User basicUser = new User(product.getUser().getId(), "Basic User", "Surname", "user@shopApp.com", "password", null, null, Role.USER, null, true, null, null);
        CurrentUser currentUser = new CurrentUser(basicUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
        assertTrue(wishListRepository.findAll().isEmpty());

        HashSet<Product> productSet = new HashSet<>();
        productSet.add(product);
        WishList entity = new WishList(1, product.getUser(), productSet);
        WishList save = wishListRepository.save(entity);
        assertNotNull(save);
        assertFalse(wishListRepository.findById(save.getId()).get().getProduct().isEmpty());

        mockMvc.perform(delete("/wishList/remove")
                .param("id",String.valueOf(save.getId()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Optional<WishList> byId = wishListRepository.findById(save.getId());
        assertTrue(byId.get().getProduct().isEmpty());

    }


    private Product createProduct(int id) {
        Product product = new Product();
        product.setId(id);
        product.setName("name");
        product.setPrice(400);
        product.setCount(40);
        product.setUser(createUser("email", "name", "surname"));
        product.setProductCode("450");
        return productRepository.save(product);
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
