package am.shopappRest.shoppingApplicationRest.userIntegrationTest;

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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test2")
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
        userRepository.deleteAll();
        {
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
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("author1@mail.com"));
        Optional<User> byEmail = userRepository.findByEmail("author1@mail.com");
        assertTrue(byEmail.isPresent());
    }

    @Test
    @WithMockUser("user@company.com")
    void createWithEx() throws Exception {
        userRepository.deleteAll();
        createUser("user@company.com","John","Doe");
        UserRegisterDto userRegisterDto = UserRegisterDto.builder()
                .email("user@company.com")
                .name("John")
                .surname("Doe")
                .password("test123")
                .gender(Gender.MALE)
                .build();

        String jsonUser = objectMapper.writeValueAsString(userRegisterDto);

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonUser)
                )
                .andExpect(status().isConflict());

        Optional<User> existingUser = userRepository.findByEmail("user@company.com");
        assertTrue(existingUser.isPresent());
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