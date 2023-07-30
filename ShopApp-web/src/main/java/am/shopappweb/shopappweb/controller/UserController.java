package am.shopappweb.shopappweb.controller;


import am.shopappweb.shopappweb.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.addressDto.AddressDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UpdatePasswordDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserRegisterDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserUpdateDto;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.mapper.UserMapper;
import am.shoppingCommon.shoppingApplication.service.MailService;
import am.shoppingCommon.shoppingApplication.service.OrderService;
import am.shoppingCommon.shoppingApplication.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * The UserController class handles HTTP requests related to user operations.
 * It provides methods for user registration, login, profile update, password change,
 * order management, address management, and email verification.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
@Validated
public class UserController {
    private final MailService mailService;
    private final UserService userService;
    private final OrderService orderService;


    /**
     * Handles the HTTP GET request for the user registration page ("/user/register").
     *
     * @param modelMap The ModelMap to be populated with the UserRegisterDto for the view.
     * @return The view name "register" to display the user registration page.
     */
    @GetMapping("/register")
    public String registerPage(ModelMap modelMap) {
        modelMap.addAttribute("userRegisterDto", UserMapper.userToUserRegisterDto(new User()));
        return "register";
    }


    /**
     * Handles the HTTP POST request for user registration ("/user/register").
     * Saves a new user using the UserService based on the provided UserRegisterDto,
     * and sends a verification email using the MailService.
     *
     * @param userRegisterDto The UserRegisterDto containing user registration details.
     * @return A redirection to the custom login page ("/customLogin").
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute UserRegisterDto userRegisterDto) {
        UserDto user = userService.save(userRegisterDto);
        if (user != null) {
            mailService.sendMailForAuth(UserMapper.userDtoToUser(user));
        }
        return "redirect:/customLogin";
    }


    /**
     * Handles the HTTP GET request to display the user profile page.
     * Populates the modelMap with data required for rendering the profile page.
     *
     * @param currentUser The authenticated user.
     * @return The view name "singleUserPage" to display the user profile page.
     */
    @GetMapping()
    public String currentUserPage(ModelMap modelmap,
                                  @AuthenticationPrincipal CurrentUser currentUser) {
        modelmap.addAttribute("userUpdateDto", new UserUpdateDto());
        modelmap.addAttribute("updatePasswordDto", new UpdatePasswordDto());
        modelmap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        return "singleUserPage";
    }


    /**
     * Handles the HTTP POST request to update the user's password.
     * Updates the user's password using the submitted UpdatePasswordDto.
     *
     * @param updatePasswordDto The UpdatePasswordDto containing the new password.
     * @param currentUser       The authenticated user.
     * @return A redirection to the user profile page after password update.
     */
    @PostMapping("/updatePassword")
    public String updatePassword(@Valid @ModelAttribute UpdatePasswordDto updatePasswordDto,
                                 @AuthenticationPrincipal CurrentUser currentUser) {
        userService.updatePassword(currentUser.getUser(), updatePasswordDto);
        return "redirect:/user";
    }


    /**
     * Handles the HTTP POST request to update the user's profile data.
     * Updates the user's profile data using the submitted UserUpdateDto.
     *
     * @param userUpdateDto The UserUpdateDto containing the updated user data.
     * @param currentUser   The authenticated user.
     * @param multipartFile The profile picture file uploaded by the user.
     * @return A redirection to the user profile page after profile update.
     * @throws IOException If an I/O error occurs during file upload.
     */
    @PostMapping("/updateUserData")
    public String updateCurrentUser(@Valid @ModelAttribute UserUpdateDto userUpdateDto,
                                    @AuthenticationPrincipal CurrentUser currentUser,
                                    @RequestParam("profile_pic") MultipartFile multipartFile) throws IOException {
        userService.updateUser(multipartFile, userUpdateDto, currentUser.getUser());
        return "redirect:/user";
    }


    /**
     * Handles the HTTP GET request to display the forgot password page.
     *
     * @return The view name "reset-password" to display the forgot password page.
     */
    @GetMapping("/forgotPassword")
    public String forgotPasswordPage() {
        return "reset-password";
    }


    /**
     * Handles the HTTP GET request to display the user's order page.
     * Populates the modelMap with order information for the authenticated user.
     *
     * @param currentUser The authenticated user.
     * @param modelMap    The ModelMap to be populated with order information for the view.
     * @return The view name "account-orders" to display the user's order page.
     */
    @GetMapping("/order")
    public String userOrderPage(@AuthenticationPrincipal CurrentUser currentUser,
                                ModelMap modelMap) {
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute(orderService.findAllByUserId(currentUser.getUser().getId()));
        return "account-orders";
    }


    /**
     * Handles the HTTP GET request to display the user's payment page.
     * Populates the modelMap with user information for the authenticated user.
     *
     * @param currentUser The authenticated user.
     * @param modelMap    The ModelMap to be populated with user information for the view.
     * @return The view name "account-payment" to display the user's payment page.
     */
    @GetMapping("/payment")
    public String userPaymentPage(@AuthenticationPrincipal CurrentUser currentUser,
                                  ModelMap modelMap) {
        modelMap.addAttribute("user", userService.findById(currentUser.getUser().getId()));
        return "account-payment";
    }


    /**
     * Handles the HTTP GET request to display the email verification page.
     * Verifies the user's email with the provided token and redirects to the custom login page.
     * If the verification fails, it redirects to the home page.
     *
     * @param email The user's email.
     * @param token The verification token.
     * @return The view name "redirect:/customLogin" if verification is successful, otherwise "redirect:/".
     */
    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("email") String email,
                              @RequestParam("token") UUID token) {
        boolean isVerified = userService.verifyUserByEmail(email, token);
        if (isVerified) {
            return "redirect:/customLogin";
        }
        return "redirect:/";
    }


    /**
     * Handles the HTTP POST request to send a forgot password email.
     * Finds the user by email, sends the forgot password email, and redirects to the home page.
     *
     * @param email The user's email.
     * @return The view name "redirect:/" to redirect to the home page after sending the email.
     */
    @PostMapping("/forgotPassword")
    public String forgotPasswordSendEmail(@RequestParam("email") String email) {
        UserDto userByEmail = userService.findByEmail(email);
        if (userByEmail != null) {
            mailService.sendMailForForgotPassword(UserMapper.userDtoToUser(userByEmail));
        }
        return "redirect:/";
    }


    /**
     * Handles the HTTP GET request to display the change password page.
     * Verifies the change password token with the provided email and token.
     * If verification is successful, it sets up the model attributes for the new password writing form.
     * If verification fails, it redirects to the home page.
     *
     * @param email    The user's email.
     * @param token    The change password token.
     * @param modelMap The ModelMap to be populated with attributes for the view.
     * @return The view name "write-new-password" if verification is successful, otherwise "redirect:/".
     */
    @GetMapping("/changePassword")
    public String changePasswordPage(@RequestParam("email") String email,
                                     @RequestParam("token") UUID token,
                                     ModelMap modelMap) {
        boolean isChanged = userService.changeUserPasswordTokenVerify(email, token.toString());
        if (isChanged) {
            modelMap.addAttribute("email", email);
            modelMap.addAttribute("token", token.toString());
            return "write-new-password";
        }
        return "redirect:/";
    }


    /**
     * Handles the HTTP POST request to reset the user's password.
     * Resets the password if the provided data is valid and redirects to the custom login page.
     * If the reset fails, it redirects to the home page.
     *
     * @param password  The new password.
     * @param password2 The confirmation of the new password.
     * @param email     The user's email.
     * @param token     The change password token.
     * @return The view name "redirect:/customLogin" if the reset is successful, otherwise "redirect:/".
     */
    @PostMapping("/changePassword")
    public String resetPassword(@RequestParam("password") String password,
                                @RequestParam("password2") String password2,
                                @RequestParam("email") String email,
                                @RequestParam("token") String token) {
        if (userService.changePassword(password, password2, email, token)) {
            return "redirect:/customLogin";
        }
        return "redirect:/";
    }


    /**
     * Handles the HTTP GET request to display the user's address page.
     * Populates the modelmap with user information containing addresses for the authenticated user.
     *
     * @param modelmap    The ModelMap to be populated with user information for the view.
     * @param currentUser The authenticated user.
     * @return The view name "account-address" to display the user's address page.
     */
    @GetMapping("/address")
    public String userAddressPage(ModelMap modelmap,
                                  @AuthenticationPrincipal CurrentUser currentUser) {
        modelmap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        return "account-address";
    }


    /**
     * Handles the HTTP POST request to add a new address for the authenticated user.
     * Saves the address if the provided data is valid and redirects to the user's address page.
     * If the address data is incomplete, it displays the user's address page again with error messages.
     *
     * @param modelmap    The ModelMap to be populated with user information for the view.
     * @param currentUser The authenticated user.
     * @param addressDto  The AddressDto containing the address data.
     * @return The view name "account-address" to display the user's address page.
     */
    @PostMapping("/address")
    public String userAddressAddPage(ModelMap modelmap,
                                     @AuthenticationPrincipal CurrentUser currentUser,
                                     @ModelAttribute AddressDto addressDto) {
        if (addressDto.getCountry().equals("") && addressDto.getCity().equals("") && addressDto.getStreet().equals("") &&
                addressDto.getUnitNumber().equals("") && addressDto.getPostCode().equals("")) {
            modelmap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
            return "account-address";
        }
        userService.saveAddress(currentUser.getUser(), addressDto);
        modelmap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        return "account-address";
    }


    /**
     * Handles the HTTP GET request to delete a user's address.
     * Removes the address from the user's address list and the address table, and redirects to the user's address page.
     *
     * @param modelmap    The ModelMap to be populated with user information for the view.
     * @param currentUser The authenticated user.
     * @param id          The ID of the address to be deleted.
     * @return The view name "account-address" to display the user's address page.
     */
    @GetMapping("/address/delete")
    public String deleteUserAddress(ModelMap modelmap,
                                    @AuthenticationPrincipal CurrentUser currentUser,
                                    @RequestParam("id") int id) {
        userService.removeAddressFromUserAndAddressTable(currentUser.getUser(), id);
        modelmap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        return "account-address";
    }
}