package am.shoppingCommon.shoppingApplication.service.impl;


import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserUpdateDto;
import am.shoppingCommon.shoppingApplication.service.AddressService;
import am.shoppingCommon.shoppingApplication.service.UserService;
import am.shoppingCommon.shoppingApplication.dto.addressDto.AddressDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UpdatePasswordDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserRegisterDto;
import am.shoppingCommon.shoppingApplication.entity.Address;
import am.shoppingCommon.shoppingApplication.entity.Role;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.mapper.AddressMapper;
import am.shoppingCommon.shoppingApplication.mapper.UserMapper;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Ashot Simonyan on 21.05.23.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressService addressService;

    @Value("${shopping-app.upload.image.path}")
    private String imageUploadPath;

    @Override
    public void remove(int id) {
        userRepository.findById(id);
    }

    @Override
    public UserDto save(UserRegisterDto userRegisterDto) {
        Optional<User> byEmail = userRepository.findByEmail(userRegisterDto.getEmail());
        if (byEmail.isEmpty()) {
            User user = UserMapper.userRegisterDtoToUser(userRegisterDto);
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setRole(Role.USER);
            user.setEnabled(false);
            user.setToken(UUID.randomUUID().toString());
            User savedUser = userRepository.save(user);
            log.info("User is created by ID: {}", savedUser.getId());
            return UserMapper.userToUserDto(savedUser);
        }
        return null;
    }

    @Override
    public UserDto saveAddress(User user, AddressDto addressDto) {
        User userFromDb = userRepository.findById(user.getId()).orElse(null);
        List<Address> addresses = userFromDb.getAddresses();
        addresses.add(AddressMapper.addressDtoToAddress(addressDto));
        userFromDb.setAddresses(addresses);
        userRepository.save(userFromDb);
        log.info("User address is created by userID: {}", user.getId());
        return UserMapper.userToUserDto(userFromDb);
    }

    @Override
    public UserDto findByEmail(String email) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            User user = byEmail.get();
            user.setToken(UUID.randomUUID().toString());
            User save = userRepository.save(user);
            return UserMapper.userToUserDto(save);
        }
        return null;
    }

    public User findByEmailForAuthentication(String email) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            User user = byEmail.get();
            user.setToken(UUID.randomUUID().toString());
            return userRepository.save(user);
        }
        return null;
    }

    @Override
    public Page<UserDto> findAll(Pageable pageable) {
        Page<User> all = userRepository.findAll(pageable);
        return UserMapper.mapPageToDto(all);
    }


    @Override
    @Transactional
    public UserDto updateUser(MultipartFile multipartFile, UserUpdateDto userUpdateDto, User currentUser) throws IOException {
        Optional<User> userOptional = userRepository.findById(currentUser.getId());
        User user = UserMapper.userUpdateDtoToUser(userUpdateDto);
        if (userOptional.isPresent()) {
            User userOldData = userOptional.get();
            if (user.getName() == null || user.getName().equals("")) {
                user.setName(userOldData.getName());
            }
            if (user.getSurname() == null || user.getSurname().equals("")) {
                user.setSurname(userOldData.getSurname());
            }
            if (user.getEmail() == null || user.getEmail().equals("")) {
                user.setEmail(userOldData.getEmail());
            }
            if (user.getPhoneNumber() == null || user.getPhoneNumber().equals("")) {
                user.setPhoneNumber(userOldData.getPhoneNumber());
            }
            if (user.getGender() == null) {
                user.setGender(userOldData.getGender());
            }
            if (user.getRole() == null) {
                user.setRole(userOldData.getRole());
            }
            if (userOldData.getProfilePic() != null) {
                user.setProfilePic(userOldData.getProfilePic());
            }
            user.setEnabled(true);
            user.setId(userOldData.getId());
            user.setPassword(userOldData.getPassword());
            user.setAddresses(userOldData.getAddresses());
            if (multipartFile != null && !multipartFile.isEmpty()) {
                String fileName = System.nanoTime() + "_" + multipartFile.getOriginalFilename();
                File file = new File(imageUploadPath + fileName);
                multipartFile.transferTo(file);
                user.setProfilePic(fileName);
            }
            User save = userRepository.save(user);
            log.info("User is updated by ID: {}", save.getId());
            return UserMapper.userToUserDto(save);
        }
        return null;
    }

    @Override
    @Transactional
    public UserDto updateUser(MultipartFile multipartFile, User currentUser) throws IOException {
        Optional<User> userOptional = userRepository.findById(currentUser.getId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (multipartFile != null && !multipartFile.isEmpty()) {
                String fileName = System.nanoTime() + "_" + multipartFile.getOriginalFilename();
                File file = new File(imageUploadPath + fileName);
                multipartFile.transferTo(file);
                user.setProfilePic(fileName);
            }
            User save = userRepository.save(user);
            return UserMapper.userToUserDto(save);
        }
        return null;
    }

    @Override
    public void removeById(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDto removeAddressFromUserAndAddressTable(User user, int id) {
        User byId = userRepository.findById(user.getId()).orElse(null);
        List<Address> addresses = byId.getAddresses();
        Address address1 = null;
        for (Address address : addresses) {
            if (address.getId() == id) {
                address1 = address;
            }
        }
        addresses.remove(address1);
        byId.setAddresses(addresses);
        User save = userRepository.save(byId);
        addressService.delete(id);
        log.info("Address is removed by user: {}", user.getId() + " " + user.getName());
        return UserMapper.userToUserDto(save);
    }

    @Override
    public UserDto findById(int id) {
        Optional<User> byId = userRepository.findById(id);
        return byId.map(UserMapper::userToUserDto).orElse(null);
    }

    @Override
    public UserDto findByIdWithAddresses(int id) {
        Optional<User> byId = userRepository.findById(id);
        return byId.map(UserMapper::userToUserDto).orElse(null);
    }


    @Override
    public UserDto updatePassword(User user, UpdatePasswordDto updatePasswordDto) {
        if (passwordEncoder.matches(updatePasswordDto.getOldPassword(), user.getPassword()) && updatePasswordDto.getPassword1().equals(updatePasswordDto.getPassword2())) {
            String encodedPassword = passwordEncoder.encode(updatePasswordDto.getPassword2());
            user.setPassword(encodedPassword);
            User save = userRepository.save(user);
            log.info("user password is updated by ID: {}", save.getId());
            return UserMapper.userToUserDto(save);
        }
        return null;
    }

    @Override
    public boolean verifyUserByEmail(String email, UUID token) {
        boolean verified = false;
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            User user = byEmail.get();
            if (!user.isEnabled() && user.getToken().equals(token.toString())) {
                user.setEnabled(true);
                user.setToken(null);
                verified = true;
                userRepository.save(user);
            }
        }
        return verified;
    }

    @Override
    public List<UserDto> findAllDeliveries() {
        List<User> byRole = userRepository.findByRole(Role.DELIVERY);
        if (!byRole.isEmpty()) {
            return UserMapper.userDtoListMap(byRole);
        }
        return null;
    }

    @Override
    public boolean changeUserPasswordTokenVerify(String email, String token) {
        boolean isVerified = false;
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            User user = byEmail.get();
            if (user.isEnabled() && user.getToken() != null && user.getToken().equals(token)) {
                isVerified = true;
            }
        }
        return isVerified;
    }

    @Override
    public boolean changePassword(String password, String password2, String email, String token) {
        boolean isChanged = false;
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            User user = byEmail.get();
            if (password.equals(password2) && user.getToken().equals(token)) {
                user.setPassword(passwordEncoder.encode(password));
                user.setToken(null);
                userRepository.save(user);
                isChanged = true;
            }
        }
        return isChanged;
    }
}
