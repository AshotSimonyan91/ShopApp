package am.shoppingCommon.shoppingApplication.service;


import am.shoppingCommon.shoppingApplication.dto.addressDto.AddressDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UpdatePasswordDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserRegisterDto;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.security.CurrentUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface UserService {

    User findByEmail(String email);

    List<User> findAll();

    void updateUser(MultipartFile multipartFile, User user, CurrentUser currentUser) throws IOException;

    boolean changeUserPasswordTokenVerify(String email, String token);

    boolean changePassword(String password, String password2, String email, String token);

    void remove(int id);

    User save(UserRegisterDto user);
    User save(User user);
    User saveAddress(CurrentUser currentUser, AddressDto addressDto);

    void removeById(int id);
    void removeAddressFromUserAndAddressTable(CurrentUser currentUser, int id);

    User findById(int id);

    User findByIdWithAddresses(int id);

    void updatePassword(User user, UpdatePasswordDto updatePasswordDto);

    boolean verifyUserByEmail(String email, UUID token);

}
