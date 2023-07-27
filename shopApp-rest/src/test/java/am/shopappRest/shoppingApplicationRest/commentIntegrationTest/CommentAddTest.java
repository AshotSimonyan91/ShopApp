package am.shopappRest.shoppingApplicationRest.commentIntegrationTest;


import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentDto;
import am.shoppingCommon.shoppingApplication.dto.commentDto.CommentRequestDto;
import am.shoppingCommon.shoppingApplication.entity.*;
import am.shoppingCommon.shoppingApplication.repository.CommentsRepository;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CommentAddTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentsRepository commentRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void addCommentTest() throws Exception {
        createUser("mail","surname","name");
        List<User> all = userRepository.findAll();
        User user = all.get(0);
        User basicUser = new User(user.getId(),"Basic User", "Surname","user@shopApp.com", "password",null,null, Role.USER,null,true,null,null);
        CurrentUser currentUser = new CurrentUser(basicUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setComment("This is a test comment.");


        MvcResult mvcResult = mockMvc.perform(post("/comments/add")
                        .param("id", String.valueOf(createProduct(123).getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("commentRequestDto", commentRequestDto))
                .andExpect(status().isOk())
                .andReturn();

        CommentDto commentDto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CommentDto.class
        );

        assertNotNull(commentDto);
        Comment savedComment = commentRepository.findById(commentDto.getId()).orElse(null);
        assertNotNull(savedComment);
        assertEquals(commentRequestDto.getComment(), savedComment.getComment());
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


