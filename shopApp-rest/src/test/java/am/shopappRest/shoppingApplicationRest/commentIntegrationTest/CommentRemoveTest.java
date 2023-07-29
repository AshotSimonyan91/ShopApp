package am.shopappRest.shoppingApplicationRest.commentIntegrationTest;

import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.entity.*;
import am.shoppingCommon.shoppingApplication.repository.CommentsRepository;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test2")
public class CommentRemoveTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentsRepository commentRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void removeCommentTest() throws Exception {
        createUser("mail","surname","name");
        List<User> all = userRepository.findAll();
        User user = all.get(0);
        User basicUser = new User(user.getId(),"Basic User", "Surname","user@shopApp.com", "password",null,null, Role.USER,null,true,null,null);
        CurrentUser currentUser = new CurrentUser(basicUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);


        Comment comment = new Comment();
        comment.setId(1);
        comment.setComment("asdad");
        comment.setUser(createUser("email","pass","name"));
        comment.setDateTime(LocalDateTime.now());
        comment.setProduct(createProduct(5));

        commentRepository.save(comment);

        MvcResult mvcResult = mockMvc.perform(delete("/comments/remove")
                        .param("comment_id", String.valueOf(comment.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        Comment deletedComment = commentRepository.findById(comment.getId()).orElse(null);
        assertNull(deletedComment, "Comment should be removed from the database");
        commentRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

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
                .id(5)
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
