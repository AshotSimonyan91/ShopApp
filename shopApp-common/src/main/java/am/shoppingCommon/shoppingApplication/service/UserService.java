package am.shoppingCommon.shoppingApplication.service;


import am.shoppingCommon.shoppingApplication.dto.addressDto.AddressDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UpdatePasswordDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserRegisterDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserUpdateDto;
import am.shoppingCommon.shoppingApplication.entity.User;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface UserService {

    UserDto findByEmail(String email);
    User findByEmailForAuthentication(String email);

    Page<UserDto> findAll(Pageable pageable);

    UserDto updateUser(MultipartFile multipartFile, UserUpdateDto userUpdateDto, User currentUser) throws IOException;
    UserDto updateUser(MultipartFile multipartFile, User currentUser) throws IOException;

    boolean changeUserPasswordTokenVerify(String email, String token);

    boolean changePassword(String password, String password2, String email, String token);

    void remove(int id);

    UserDto save(UserRegisterDto user);

    UserDto saveAddress(User user, AddressDto addressDto);

    void removeById(int id);

    UserDto removeAddressFromUserAndAddressTable(User currentUser, int id);

    UserDto findById(int id);

    UserDto findByIdWithAddresses(int id);

    UserDto updatePassword(User user, UpdatePasswordDto updatePasswordDto);

    boolean verifyUserByEmail(String email, UUID token);

    List<UserDto> findAllDeliveries();

}
