package am.shopappRest.shoppingApplicationRest.UserRegisterTest;

import am.shoppingCommon.shoppingApplication.dto.userDto.UserRegisterDto;
import am.shoppingCommon.shoppingApplication.entity.Gender;
import am.shoppingCommon.shoppingApplication.entity.Role;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserRegisterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithMockUser("user@company.com")
    void create() throws Exception {
        createTestUser();
        UserRegisterDto userRegisterDto = UserRegisterDto.builder()
                .email("author1@mail.com")
                .name("Poxos")
                .surname("poxosyan")
                .password("test")
                .gender(Gender.FEMALE)
                .build();
        String jsonAuthor = new ObjectMapper().writeValueAsString(userRegisterDto);
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonAuthor)
                ).andExpect(status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("author1@mail.com"));
        Optional<User> byEmail = userRepository.findByEmail("author1@mail.com");
        assertTrue(byEmail.isPresent());
    }

    private void createTestUser() {
        userRepository.save(User.builder()
                .id(1)
                .email("user@company.com")
                .name("poxos")
                .surname("poxosyan")
                .password("poxos")
                .role(Role.USER)
                .enabled(true)
                .gender(Gender.FEMALE)
                .build());
    }
}