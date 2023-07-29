package am.shopappRest.shoppingApplicationRest.deliveryIntegrationTest;

import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.entity.*;
import am.shoppingCommon.shoppingApplication.repository.DeliveryRepository;
import am.shoppingCommon.shoppingApplication.repository.OrderRepository;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Ashot Simonyan on 29.07.23.
 */

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test2")
public class DeliveryAddTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @BeforeEach
    public void before(){
        deliveryRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    public void deliveryAdd() throws Exception {
        Product product = createProduct(33);

        User basicUser = new User(product.getUser().getId(), "Basic User", "Surname", "user@shopApp.com", "password", null, null, Role.USER, null, true, null, null);
        CurrentUser currentUser = new CurrentUser(basicUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        Order order = new Order(1, LocalDateTime.now(),33.3, Status.APPROVED,product.getUser(),null);
        Order save = orderRepository.save(order);

        mockMvc.perform(get("/delivery/add")
                .contentType(MediaType.APPLICATION_JSON)
                .param("order_id",String.valueOf(save.getId())))
                .andExpect(status().isOk());

        List<Order> allByUserId = orderRepository.findAllByUserId(product.getUser().getId());
        assertNotNull(allByUserId);
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
