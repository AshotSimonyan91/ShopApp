package am.shoppingCommon.shoppingApplication.service.impl;


import am.shoppingCommon.shoppingApplication.dto.addressDto.AddressDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UpdatePasswordDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserRegisterDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserUpdateDto;
import am.shoppingCommon.shoppingApplication.entity.Address;
import am.shoppingCommon.shoppingApplication.entity.Role;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.exception.EmailAlreadyExistsException;
import am.shoppingCommon.shoppingApplication.mapper.AddressMapper;
import am.shoppingCommon.shoppingApplication.mapper.UserMapper;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import am.shoppingCommon.shoppingApplication.service.AddressService;
import am.shoppingCommon.shoppingApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

/**
 * Service implementation class responsible for handling user-related operations.
 * It implements the UserService interface and provides functionalities for managing users, their addresses, and account registration.
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

    /**
     * Removes a user with the given ID from the database.
     *
     * @param id The ID of the user to remove.
     */
    @Override
    public void remove(int id) {
        userRepository.findById(id);
    }


    /**
     * Saves a new user to the database based on the provided UserRegisterDto.
     * The user's password is encoded using the passwordEncoder before saving.
     *
     * @param userRegisterDto The UserRegisterDto containing user registration data.
     * @return The UserDto of the newly created user if successful, null otherwise.
     */
    @Override
    public UserDto save(UserRegisterDto userRegisterDto) {
        if (userRegisterDto == null) {
            return null;
        }

        Optional<User> byEmail = userRepository.findByEmail(userRegisterDto.getEmail());
        if (byEmail.isPresent()) {
            return null;
        }
        User user;
        user = UserMapper.userRegisterDtoToUser(userRegisterDto);
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setRole(Role.USER);
        user.setEnabled(false);
        user.setToken(UUID.randomUUID().toString());
        user = userRepository.save(user);
        log.info("User is created by ID: {}", user.getId());

        return UserMapper.userToUserDto(user);
    }


    /**
     * Saves a new address for the given user to the database based on the provided AddressDto.
     *
     * @param user       The user for whom to add the address.
     * @param addressDto The AddressDto containing the address details to be added.
     * @return The updated UserDto containing the user's new address if successful, null otherwise.
     */
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


    /**
     * Finds a user by their email address.
     * If a user is found with the provided email, a new UUID token is generated and saved to the user's account.
     *
     * @param email The email address of the user to find.
     * @return The UserDto of the found user with a newly generated token, or null if no user is found.
     */
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


    /**
     * Finds a user by their email address for authentication purposes.
     * If a user is found with the provided email, a new UUID token is generated and saved to the user's account.
     *
     * @param email The email address of the user to find.
     * @return The User entity of the found user with a newly generated token, or null if no user is found.
     */
    public User findByEmailForAuthentication(String email) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            User user = byEmail.get();
            user.setToken(UUID.randomUUID().toString());
            return userRepository.save(user);
        }
        return null;
    }

    /**
     * Retrieves a page of users from the database based on the provided Pageable object.
     *
     * @param pageable The pageable object used to control pagination.
     * @return A Page of UserDtos containing the users found.
     */
    @Override
    public Page<UserDto> findAll(Pageable pageable) {
        Page<User> all = userRepository.findAll(pageable);
        return UserMapper.mapPageToDto(all);
    }


    /**
     * Updates the user's profile information based on the provided UserUpdateDto and profile picture (MultipartFile).
     * If a profile picture is provided, it will be uploaded and the file name will be saved in the user's profilePic field.
     *
     * @param multipartFile The profile picture file to be uploaded.
     * @param userUpdateDto The UserUpdateDto containing updated user information.
     * @param currentUser   The current authenticated user.
     * @return The updated UserDto if successful, null otherwise.
     * @throws IOException If there is an issue with the profile picture file upload.
     */
    @Override
    @Transactional
    public UserDto updateUser(MultipartFile multipartFile, UserUpdateDto userUpdateDto, User currentUser) throws IOException {
        // Retrieve the user from the database based on the current user's ID
        Optional<User> userOptional = userRepository.findById(currentUser.getId());
        User user = UserMapper.userUpdateDtoToUser(userUpdateDto);
        if (userOptional.isPresent()) {
            User userOldData = userOptional.get();
            Optional<User> byEmail = userRepository.findByEmail(user.getEmail());
            if (byEmail.isPresent()) {
                throw new EmailAlreadyExistsException("Email is already exists");
            }
            // Preserve existing data if no new data is provided in the UserUpdateDto
            if (user.getEmail() == null || user.getEmail().equals("")) {
                user.setEmail(userOldData.getEmail());
            }
            if (user.getName() == null || user.getName().equals("")) {
                user.setName(userOldData.getName());
            }
            if (user.getSurname() == null || user.getSurname().equals("")) {
                user.setSurname(userOldData.getSurname());
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
            // Preserve existing profile picture if not updated
            if (userOldData.getProfilePic() != null) {
                user.setProfilePic(userOldData.getProfilePic());
            }
            user.setEnabled(true);
            user.setId(userOldData.getId());
            user.setPassword(userOldData.getPassword());
            user.setAddresses(userOldData.getAddresses());
            // If a new profile picture is provided, upload it and save the file name in the user's profilePic field
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


    /**
     * Updates the user's profile picture based on the provided MultipartFile.
     * If a new profile picture is provided, it will be uploaded and the file name will be saved in the user's profilePic field.
     *
     * @param multipartFile The profile picture file to be uploaded.
     * @param currentUser   The current authenticated user.
     * @return The updated UserDto if successful, null otherwise.
     * @throws IOException If there is an issue with the profile picture file upload.
     */
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


    /**
     * Removes a user from the database based on the provided ID.
     *
     * @param id The ID of the user to be removed.
     */
    @Override
    public void removeById(int id) {
        userRepository.deleteById(id);
    }


    /**
     * Removes an address from a user's account and also deletes the address from the address table.
     *
     * @param user The user from whom to remove the address.
     * @param id   The ID of the address to be removed.
     * @return The updated UserDto after removing the address if successful, null otherwise.
     */
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

    /**
     * Finds a user by their ID.
     *
     * @param id The ID of the user to find.
     * @return The UserDto of the found user if successful, null otherwise.
     */
    @Override
    public UserDto findById(int id) {
        Optional<User> byId = userRepository.findById(id);
        return byId.map(UserMapper::userToUserDto).orElse(null);
    }


    /**
     * Finds a user by their ID along with their addresses.
     *
     * @param id The ID of the user to find.
     * @return The UserDto of the found user along with their addresses if successful, null otherwise.
     */
    @Override
    public UserDto findByIdWithAddresses(int id) {
        Optional<User> byId = userRepository.findById(id);
        return byId.map(UserMapper::userToUserDto).orElse(null);
    }


    /**
     * Updates the user's password based on the provided UpdatePasswordDto.
     *
     * @param user              The user whose password is to be updated.
     * @param updatePasswordDto The UpdatePasswordDto containing the old and new password information.
     * @return The updated UserDto if successful, null otherwise.
     */
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

    /**
     * Verifies a user's email based on the provided email and token.
     * If the email and token match and the user is not enabled, the user's account will be enabled.
     *
     * @param email The email address of the user to verify.
     * @param token The token to verify the user.
     * @return true if the user is verified and enabled, false otherwise.
     */
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

    /**
     * Retrieves a list of users with the role "DELIVERY".
     *
     * @return A list of UserDtos containing the delivery users if successful, null otherwise.
     */
    @Override
    public List<UserDto> findAllDeliveries() {
        List<User> byRole = userRepository.findByRole(Role.DELIVERY);
        if (!byRole.isEmpty()) {
            return UserMapper.userDtoListMap(byRole);
        }
        return null;
    }

    /**
     * Verifies if the given email and token combination is valid for changing the user's password.
     *
     * @param email The email address of the user.
     * @param token The token provided for password change verification.
     * @return true if the email and token are valid for password change, false otherwise.
     */
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

    /**
     * Changes the user's password if the provided password and token are valid.
     *
     * @param password  The new password to be set.
     * @param password2 The confirmation of the new password.
     * @param email     The email address of the user.
     * @param token     The token provided for password change verification.
     * @return true if the password is successfully changed, false otherwise.
     */
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
