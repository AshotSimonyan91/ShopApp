package am.shopappweb.shopappweb.controller;


import am.shopappweb.shopappweb.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.addressDto.AddressDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UpdatePasswordDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserRegisterDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserUpdateDto;
import am.shoppingCommon.shoppingApplication.entity.Order;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.mapper.NotificationMapper;
import am.shoppingCommon.shoppingApplication.mapper.OrderMapper;
import am.shoppingCommon.shoppingApplication.mapper.UserMapper;
import am.shoppingCommon.shoppingApplication.service.MailService;
import am.shoppingCommon.shoppingApplication.service.NotificationService;
import am.shoppingCommon.shoppingApplication.service.OrderService;
import am.shoppingCommon.shoppingApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
@Validated
public class UserController {
    private final MailService mailService;
    private final UserService userService;
    private final OrderService orderService;
    private final NotificationService notificationService;

    @Value("${site.url}")
    private String siteUrl;

    @GetMapping("/register")
    public String registerPage(ModelMap modelMap) {
        modelMap.addAttribute("userRegisterDto", UserMapper.userToUserRegisterDto(new User()));
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute UserRegisterDto userRegisterDto, Errors errors) {
        if (errors.hasErrors()) {
            return "register";
        }
        User user = userService.save(userRegisterDto);
        if (user != null) {
            mailService.sendMail(userRegisterDto.getEmail(), "Welcome",
                    "Hi " + userRegisterDto.getName() +
                            " Welcome please verify your account by clicking " + siteUrl + "/user/verify?email=" + user.getEmail() + "&token=" + user.getToken()
            );
        }
        return "redirect:/customLogin";
    }

    @GetMapping()
    public String currentUserPage(ModelMap modelmap,
                                  @AuthenticationPrincipal CurrentUser currentUser) {
        modelmap.addAttribute("userUpdateDto", new UserUpdateDto());
        modelmap.addAttribute("updatePasswordDto", new UpdatePasswordDto());
        modelmap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        return "singleUserPage";
    }


    @PostMapping("/updatePassword")
    public String updatePassword(@Valid @ModelAttribute UpdatePasswordDto updatePasswordDto,
                                 @AuthenticationPrincipal CurrentUser currentUser) {
        userService.updatePassword(currentUser.getUser(), updatePasswordDto);
        return "redirect:/user";
    }


    @PostMapping("/updateUserData")
    public String updateCurrentUser(@Valid @ModelAttribute UserUpdateDto userUpdateDto,
                                    @AuthenticationPrincipal CurrentUser currentUser,
                                    @RequestParam("profile_pic") MultipartFile multipartFile) throws IOException {
        userService.updateUser(multipartFile, UserMapper.userUpdateDtoToUser(userUpdateDto), currentUser.getUser());
        return "redirect:/user";
    }

    @GetMapping("/notifications/{userId}")
    public String notificationPage(ModelMap modelmap,
                                   @PathVariable("userId") int id,
                                   @AuthenticationPrincipal CurrentUser currentUser) {
        modelmap.addAttribute("notifications", NotificationMapper.map(notificationService.findAllByUserId(id)));
        modelmap.addAttribute("currentUser", UserMapper.userToUserDto(currentUser.getUser()));
        return "notifications";
    }

    @GetMapping("/forgotPassword")
    public String forgotPasswordPage() {
        return "reset-password";
    }


    @GetMapping("/order")
    public String userOrderPage(@AuthenticationPrincipal CurrentUser currentUser,
                                ModelMap modelMap) {
        List<Order> allByUserId = orderService.findAllByUserId(currentUser.getUser().getId());
        modelMap.addAttribute("user", UserMapper.userToUserDto(currentUser.getUser()));
        modelMap.addAttribute(OrderMapper.listOrderToListOrderDto(allByUserId));
        return "account-orders";
    }

    @GetMapping("/payment")
    public String userPaymentPage(@AuthenticationPrincipal CurrentUser currentUser,
                                  ModelMap modelMap) {
        modelMap.addAttribute("user", UserMapper.userToUserDto(currentUser.getUser()));
        return "account-payment";
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("email") String email,
                              @RequestParam("token") UUID token) {
        boolean isVerified = userService.verifyUserByEmail(email, token);
        if (isVerified) {
            return "redirect:/customLogin";
        }
        return "redirect:/";
    }


    @PostMapping("/forgotPassword")
    public String forgotPassword(@RequestParam("email") String email) {
        User userByEmail = userService.findByEmail(email);
        if (userByEmail != null) {
            mailService.sendMail(userByEmail.getEmail(), "Welcome",
                    "Hi " + userByEmail.getName() +
                            " Welcome please for change password by click " + siteUrl + "/user/changePassword?email=" + userByEmail.getEmail() + "&token=" + userByEmail.getToken()
            );
        }
        return "redirect:/";
    }

    @GetMapping("/changePassword")
    public String changePassword(@RequestParam("email") String email,
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

    @GetMapping("/address")
    public String userAddressPage(ModelMap modelmap,
                                  @AuthenticationPrincipal CurrentUser currentUser) {
        modelmap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        return "account-address";
    }

    @PostMapping("/address")
    public String userAddressAddPage(ModelMap modelmap,
                                     @AuthenticationPrincipal CurrentUser currentUser,
                                     @ModelAttribute AddressDto addressDto) {
        if (addressDto.getCountry().equals("") && addressDto.getCity().equals("") && addressDto.getStreet().equals("") &&
                addressDto.getUnitNumber().equals("") && addressDto.getPostCode().equals("")) {
            modelmap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
            return "account-address";
        }
        userService.save(userService.saveAddress(currentUser.getUser(), addressDto));
        modelmap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        return "account-address";
    }


    @GetMapping("/address/delete")
    public String deleteUserAddress(ModelMap modelmap,
                                    @AuthenticationPrincipal CurrentUser currentUser,
                                    @RequestParam("id") int id) {
        userService.removeAddressFromUserAndAddressTable(currentUser.getUser(), id);
        modelmap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        return "account-address";
    }

}