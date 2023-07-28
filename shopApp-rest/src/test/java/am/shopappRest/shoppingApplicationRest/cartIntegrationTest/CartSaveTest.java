package am.shopappRest.shoppingApplicationRest.cartIntegrationTest;


import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.entity.Gender;
import am.shoppingCommon.shoppingApplication.entity.Role;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.repository.CartRepository;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CartSaveTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;


    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSaveCart() throws Exception {
        cartRepository.deleteAll();;
        productRepository.deleteAll();
        userRepository.deleteAll();

        int productId = 123;

        User user = createUser("mail", "name", "surname");

        User basicUser = new User(user.getId(), "Basic User", "Surname", "user@shopApp.com", "password", null, null, Role.USER, null, true, null, null);
        CurrentUser currentUser = new CurrentUser(basicUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
        assertTrue(cartRepository.findAll().isEmpty());


        mockMvc.perform(get("/cart/add/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        assertFalse(cartRepository.findAll().isEmpty());
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
