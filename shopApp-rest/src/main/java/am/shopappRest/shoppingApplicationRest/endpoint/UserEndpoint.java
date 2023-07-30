package am.shopappRest.shoppingApplicationRest.endpoint;


import am.shopappRest.shoppingApplicationRest.restDto.userAuthDto.UserAuthRequestDto;
import am.shopappRest.shoppingApplicationRest.restDto.userAuthDto.UserAuthResponseDto;
import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shopappRest.shoppingApplicationRest.util.JwtTokenUtil;
import am.shoppingCommon.shoppingApplication.dto.addressDto.AddressDto;
import am.shoppingCommon.shoppingApplication.dto.notificationDto.NotificationDto;
import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UpdatePasswordDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserRegisterDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserUpdateDto;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.mapper.UserMapper;
import am.shoppingCommon.shoppingApplication.service.MailService;
import am.shoppingCommon.shoppingApplication.service.NotificationService;
import am.shoppingCommon.shoppingApplication.service.OrderService;
import am.shoppingCommon.shoppingApplication.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


/**
 * RestController class responsible for handling user-related API operations.
 * It provides endpoints for user authentication, registration, user data retrieval and updates,
 * password updates, user image updates, notifications retrieval, order retrieval, email verification,
 * password reset, and address management.
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserEndpoint {

    private final MailService mailService;
    private final UserService userService;
    private final OrderService orderService;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil tokenUtil;

    @Value("${site.url.web}")
    private String siteUrl;


    /**
     * Authenticates the user based on the provided UserAuthRequestDto.
     *
     * @param userAuthRequestDto The DTO containing user authentication details (email and password).
     * @return ResponseEntity with the UserAuthResponseDto containing the JWT token as a JSON response upon successful authentication.
     * If the user is not found or the provided password does not match the stored password, it returns UNAUTHORIZED (401).
     */
    @PostMapping("/auth")
    public ResponseEntity<UserAuthResponseDto> auth(@RequestBody UserAuthRequestDto userAuthRequestDto) {
        User user = userService.findByEmailForAuthentication(userAuthRequestDto.getEmail());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!passwordEncoder.matches(userAuthRequestDto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = tokenUtil.generateToken(userAuthRequestDto.getEmail());
        return ResponseEntity.ok(new UserAuthResponseDto(token));
    }


    /**
     * Registers a new user based on the provided UserRegisterDto.
     *
     * @param userRegisterDto The DTO containing user registration details (email, password, etc.).
     * @return ResponseEntity with the created UserDto as a JSON response.
     * If a user with the provided email already exists, it returns CONFLICT (409).
     */
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserRegisterDto userRegisterDto) {
        UserDto byEmail = userService.findByEmail(userRegisterDto.getEmail());
        if (byEmail != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        userService.save(userRegisterDto);
        return ResponseEntity.ok(UserMapper.userRegisterDtoToUerDto(userRegisterDto));
    }


    /**
     * Retrieves the user's data along with addresses based on the authenticated user's information.
     *
     * @param currentUser The information of the currently authenticated user.
     * @return ResponseEntity with the UserDto containing the user's data and addresses as a JSON response.
     */
    @GetMapping()
    public ResponseEntity<UserDto> getUserWithAddress(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(userService.findByIdWithAddresses(currentUser.getUser().getId()));
    }


    /**
     * Updates the user's password based on the provided UpdatePasswordDto.
     *
     * @param updatePasswordDto The DTO containing the new and old passwords.
     * @param currentUser       The information of the currently authenticated user.
     * @return ResponseEntity with the updated UserDto as a JSON response.
     */
    @PutMapping("/updatePassword")
    public ResponseEntity<UserDto> updatePassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto,
                                                  @AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(userService.updatePassword(currentUser.getUser(), updatePasswordDto));
    }

    /**
     * Updates the user's data based on the provided UserUpdateDto and image file.
     *
     * @param userUpdateDto The DTO containing updated user data.
     * @param currentUser   The information of the currently authenticated user.
     * @param multipartFile The MultipartFile representing the user's profile picture.
     * @return ResponseEntity with the updated UserDto as a JSON response.
     * @throws IOException if there is an error while processing the image file.
     */
    @PutMapping("/updateUserData")
    public ResponseEntity<UserDto> updateCurrentUserData(@Valid @RequestBody UserUpdateDto userUpdateDto,
                                                         @AuthenticationPrincipal CurrentUser currentUser, @RequestParam("profile_pic") MultipartFile multipartFile) throws IOException {
        return ResponseEntity.ok(userService.updateUser(multipartFile, userUpdateDto, currentUser.getUser()));
    }


    /**
     * Updates the user's profile image based on the provided MultipartFile.
     *
     * @param currentUser   The information of the currently authenticated user.
     * @param multipartFile The MultipartFile representing the user's profile picture.
     * @return ResponseEntity with the updated UserDto containing the profile image URL as a JSON response.
     * @throws IOException if there is an error while processing the image file.
     */
    @PostMapping("/updateUserData/image")
    public ResponseEntity<UserDto> updateCurrentUserImage(@AuthenticationPrincipal CurrentUser currentUser,
                                                          @RequestParam("profile_pic") MultipartFile multipartFile) throws IOException {
        UserDto body = userService.updateUser(multipartFile, currentUser.getUser());
        body.setProfilePic(siteUrl + "/getImage?profilePic=" + body.getProfilePic());
        return ResponseEntity.ok(body);
    }


    /**
     * Retrieves all notifications for the user with the given ID.
     *
     * @param id The ID of the user to retrieve notifications for.
     * @return ResponseEntity with the list of NotificationDto representing the user's notifications as a JSON response.
     */
    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<NotificationDto>> getUserAllNotifications(@PathVariable("userId") int id) {
        return ResponseEntity.ok(notificationService.findAllByUserId(id));
    }

    /**
     * Retrieves all orders for the currently authenticated user.
     *
     * @param currentUser The information of the currently authenticated user.
     * @return ResponseEntity with the list of OrderDto representing the user's orders as a JSON response.
     */
    @GetMapping("/order")
    public ResponseEntity<List<OrderDto>> getUserOrders(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(orderService.findAllByUserId(currentUser.getUser().getId()));
    }

    /**
     * Verifies the user's email based on the provided email and token.
     *
     * @param email The email of the user to be verified.
     * @param token The token associated with the email verification request.
     * @return ResponseEntity with no content (200 OK) if the user's email is successfully verified.
     * If the email and token combination is invalid, it returns UNAUTHORIZED (401).
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam("email") String email,
                                         @RequestParam("token") UUID token) {
        if (userService.verifyUserByEmail(email, token)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Initiates the process of resetting the user's password by sending a reset email.
     *
     * @param email The email of the user for whom the password reset is requested.
     * @return ResponseEntity with the user's reset token as a string in the response body if the email is found and the reset email is sent.
     * If the email does not exist, it returns NOT_FOUND (404).
     */
    @GetMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        UserDto userByEmail = userService.findByEmail(email);
        if (userByEmail != null) {
            mailService.sendMailForForgotPassword(UserMapper.userDtoToUser(userByEmail));
            return ResponseEntity.ok(userByEmail.getToken());
        }
        return ResponseEntity.notFound().build();
    }


    /**
     * Resets the user's password based on the provided new password, email, and token.
     *
     * @param password  The new password for the user.
     * @param password2 The confirmation of the new password.
     * @param email     The email of the user for whom the password reset is requested.
     * @param token     The token associated with the password reset request.
     * @return ResponseEntity with no content (200 OK) if the user's password is successfully reset.
     * If the email and token combination is invalid or the passwords do not match, it returns CONFLICT (409).
     */
    @PutMapping("/changePassword")
    public ResponseEntity<?> resetPassword(@RequestParam("password") String password,
                                           @RequestParam("password2") String password2,
                                           @RequestParam("email") String email,
                                           @RequestParam("token") String token) {
        if (userService.changePassword(password, password2, email, token)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    /**
     * Adds a new address to the user's address list.
     *
     * @param currentUser The information of the currently authenticated user.
     * @param addressDto  The DTO containing details of the new address to be added.
     * @return ResponseEntity with the updated UserDto containing the added address as a JSON response.
     */
    @PostMapping("/address")
    public ResponseEntity<UserDto> addUserAddress(@AuthenticationPrincipal CurrentUser currentUser,
                                                  @RequestBody AddressDto addressDto) {
        return ResponseEntity.ok(userService.saveAddress(currentUser.getUser(), addressDto));
    }


    /**
     * Deletes a specific address from the user's address list based on the provided address ID.
     *
     * @param currentUser The information of the currently authenticated user.
     * @param id          The ID of the address to be deleted.
     * @return ResponseEntity with the updated UserDto containing the removed address as a JSON response.
     */
    @DeleteMapping("/address/delete")
    public ResponseEntity<UserDto> deleteUserAddress(@AuthenticationPrincipal CurrentUser currentUser,
                                                     @RequestParam("id") int id) {
        return ResponseEntity.ok(userService.removeAddressFromUserAndAddressTable(currentUser.getUser(), id));
    }

}
