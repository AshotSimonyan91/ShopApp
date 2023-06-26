package am.shopappweb.shopappweb.service.impl;


import am.shopappweb.shopappweb.security.CurrentUser;
import am.shopappweb.shopappweb.service.AddressService;
import am.shopappweb.shopappweb.service.UserService;
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
    public User save(UserRegisterDto userRegisterDto) {
        Optional<User> byEmail = userRepository.findByEmail(userRegisterDto.getEmail());
        if (byEmail.isEmpty()) {
            User user = UserMapper.userRegisterDtoToUser(userRegisterDto);
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setRole(Role.USER);
            user.setEnabled(false);
            user.setToken(UUID.randomUUID().toString());
            User savedUser = userRepository.save(user);
            return savedUser;
        }
        return null;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User saveAddress(CurrentUser currentUser, AddressDto addressDto) {
        User user = currentUser.getUser();
        User byId = userRepository.findById(user.getId()).orElse(null);
        List<Address> addresses = byId.getAddresses();
        addresses.add(AddressMapper.addressDtoToAddress(addressDto));
        byId.setAddresses(addresses);
        return byId;
    }

    @Override
    public User findByEmail(String email) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            User user = byEmail.get();
            user.setToken(UUID.randomUUID().toString());
            return userRepository.save(user);
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void updateUser(MultipartFile multipartFile, User user, CurrentUser currentUser) throws IOException {
        Optional<User> userOptional = userRepository.findById(currentUser.getUser().getId());
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
            if (multipartFile != null && !multipartFile.isEmpty()) {
                String fileName = System.nanoTime() + "_" + multipartFile.getOriginalFilename();
                File file = new File(imageUploadPath + fileName);
                multipartFile.transferTo(file);
                user.setProfilePic(fileName);
            }
            userRepository.save(user);
        }
    }


    @Override
    public void removeById(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public void removeAddressFromUserAndAddressTable(CurrentUser currentUser, int id) {
        User user = currentUser.getUser();
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
        userRepository.save(byId);
        addressService.delete(id);
    }

    @Override
    public User findById(int id) {
        Optional<User> byId = userRepository.findById(id);
        return byId.orElse(null);
    }

    @Override
    public User findByIdWithAddresses(int id) {
        Optional<User> byId = userRepository.findById(id);
        return byId.get();
    }


    @Override
    public void updatePassword(User user, UpdatePasswordDto updatePasswordDto) {
        if (passwordEncoder.matches(updatePasswordDto.getOldPassword(), user.getPassword()) && updatePasswordDto.getPassword1().equals(updatePasswordDto.getPassword2())) {
            String encodedPassword = passwordEncoder.encode(updatePasswordDto.getPassword2());
            user.setPassword(encodedPassword);
            userRepository.save(user);
        }
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

    public void deleteProfilePicture(String existingProfilePic) {
        if (existingProfilePic != null) {
            File file = new File(imageUploadPath + existingProfilePic);
            if (file.exists()) {
                boolean delete = file.delete();
                System.out.println(delete);
            }
        }
    }
}
