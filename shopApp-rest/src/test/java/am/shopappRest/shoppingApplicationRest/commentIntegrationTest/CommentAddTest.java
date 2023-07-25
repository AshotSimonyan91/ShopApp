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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
        User basicUser = new User(50,"Basic User", "Surname","user@shopApp.com", "password",null,null, Role.USER,null,true,null,null);
        CurrentUser currentUser = new CurrentUser(basicUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Assuming you have a CommentRequestDto ready for testing
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setComment("This is a test comment.");

        // Assuming you have an 'id' value for the associated entity (e.g., Post or Article)

        int id = 123;
        // Perform the POST request and expect HTTP status 200 (OK)
        MvcResult mvcResult = mockMvc.perform(post("/comments/add")
                        .param("id", String.valueOf(createProduct(123).getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("commentRequestDto", commentRequestDto)) // Set commentRequestDto as @ModelAttribute
                .andExpect(status().isOk())
                .andReturn();

        // Parse the response content to extract the CommentDto
        CommentDto commentDto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CommentDto.class
        );

        // Perform assertions on the CommentDto or any other validation you need
        assertNotNull(commentDto);
        // Add more assertions as needed based on the response structure

        // Optionally, you can also verify the behavior in the database
        // For example, you can use a repository to fetch the saved comment and assert its content
        // Example assuming you have a CommentRepository
        Comment savedComment = commentRepository.findById(commentDto.getId()).orElse(null);
        assertNotNull(savedComment);
        assertEquals(commentRequestDto.getComment(), savedComment.getComment());
        commentRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }
    @Test
    void removeCommentTest() throws Exception {
        User basicUser = new User(50,"Basic User", "Surname","user@shopApp.com", "password",null,null, Role.USER,null,true,null,null);
        CurrentUser currentUser = new CurrentUser(basicUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Assuming you have a comment with ID 1 in the database that you want to delete
        // You need to set up the comment with ID 1 for the test
        Comment comment = new Comment();
        comment.setId(1);
        comment.setComment("asdad");
        comment.setUser(createUser("email","pass","name"));
        comment.setDateTime(LocalDateTime.now());
        comment.setProduct(createProduct(5));
        // Set up other properties of the comment if needed
        // Save the comment in the database
        commentRepository.save(comment);

        // Perform the DELETE request and expect HTTP status 204 (No Content)
        MvcResult mvcResult = mockMvc.perform(delete("/comments/remove")
                        .param("comment_id", String.valueOf(comment.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        // Verify that the comment is removed from the database
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


