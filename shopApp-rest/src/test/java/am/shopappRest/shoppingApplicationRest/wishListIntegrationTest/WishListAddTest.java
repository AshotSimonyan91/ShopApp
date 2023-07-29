package am.shopappRest.shoppingApplicationRest.wishListIntegrationTest;

import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.entity.Gender;
import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.entity.Role;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import am.shoppingCommon.shoppingApplication.repository.WishListRepository;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Ashot Simonyan on 29.07.23.
 */

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test2")
public class WishListAddTest {

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void before(){
        wishListRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void wishListAdd() throws Exception {
		Product product = createProduct(33);

        User basicUser = new User(product.getUser().getId(), "Basic User", "Surname", "user@shopApp.com", "password", null, null, Role.USER, null, true, null, null);
        CurrentUser currentUser = new CurrentUser(basicUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
        assertTrue(wishListRepository.findAll().isEmpty());

        mockMvc.perform(post("/wishList/add")
                .param("productId",String.valueOf(product.getId()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertFalse(wishListRepository.findAll().isEmpty());

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
