package am.shoppingCommon.shoppingApplication.userTest;

import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserRegisterDto;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import am.shoppingCommon.shoppingApplication.service.AddressService;
import am.shoppingCommon.shoppingApplication.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class UserSaveTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, passwordEncoder, addressService);
    }

    @Test
    public void testSaveUser_Success() {
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setEmail("test@example.com");
        userRegisterDto.setPassword("password123");

        User user = new User();
        user.setId(1);

        when(userRepository.findByEmail(userRegisterDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(user);

        UserDto result = userService.save(userRegisterDto);

        assertEquals(1, result.getId());
    }
    @Test
    public void testSaveUser_NullInputDto() {
        UserDto result = userService.save(null);
        assertNull(result);
    }
    @Test
    public void testSaveUser_UserWithEmailExists() {
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setEmail("existing@example.com");
        userRegisterDto.setPassword("password123");

        User existingUser = new User();
        existingUser.setEmail("existing@example.com");

        when(userRepository.findByEmail(userRegisterDto.getEmail())).thenReturn(Optional.of(existingUser));

        UserDto result = userService.save(userRegisterDto);

        assertNull(result);
    }
}


