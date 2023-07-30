package am.shopappRest.shoppingApplicationRest.productIntegrationTest;

import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.dto.imageDto.ImageDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductRequestDto;
import am.shoppingCommon.shoppingApplication.entity.Category;
import am.shoppingCommon.shoppingApplication.entity.Gender;
import am.shoppingCommon.shoppingApplication.entity.Role;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.mapper.CategoryMapper;
import am.shoppingCommon.shoppingApplication.repository.CategoryRepository;
import am.shoppingCommon.shoppingApplication.repository.ImagesRepository;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductSaveTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImagesRepository imagesRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    @AfterEach
    public void tearDown() {
        imagesRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    public void testAddProduct() throws Exception {
        Category category = createCategory();
        createUser("mail", "surname", "name");
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        categoryDtoList.add(CategoryMapper.categoryToDto(category));
        List<User> all = userRepository.findAll();
        User user = all.get(0);

        CreateProductRequestDto createProductRequestDto = new CreateProductRequestDto();
        createProductRequestDto.setName("Test Product");
        createProductRequestDto.setBrand("Test Brand");
        createProductRequestDto.setCategories(categoryDtoList);
        createProductRequestDto.setProductCode("TEST-123");
        createProductRequestDto.setDescription("This is a test product.");
        createProductRequestDto.setCount(10);
        createProductRequestDto.setPrice(99.99);

        List<ImageDto> images = new ArrayList<>();
        createProductRequestDto.setImages(images);

        User basicUser = new User(user.getId(), "Basic User", "Surname", "user@shopApp.com", "password", null, null, Role.USER, null, true, null, null);
        CurrentUser currentUser = new CurrentUser(basicUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String imagePath = "C:/Users/dell/IdeaProjects/ShopApp2/shopApp-rest/src/test/java/am/shopappRest/shoppingApplicationRest/images/headphone.jpg";
        File imageFile = new File(imagePath);

        MockMultipartFile image = new MockMultipartFile("files", "headphone.jpg", "image/jpeg", new FileInputStream(imageFile));

        MockMultipartFile[] files = {image};
        String jsonRequest = objectMapper.writeValueAsString(createProductRequestDto);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/products/add")
                .file(files[0])
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .with(user(currentUser)));

        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.brand").value("Test Brand"))
                .andExpect(jsonPath("$.productCode").value("TEST-123"))
                .andExpect(jsonPath("$.description").value("This is a test product."))
                .andExpect(jsonPath("$.count").value(10))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.images").isArray())
                .andExpect(jsonPath("$.categories").isArray());
    }

    private Category createCategory() {
        Category category = new Category();
        category.setName("test");
        return categoryRepository.save(category);
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
