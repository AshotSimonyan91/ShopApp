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

    @Value("${site.url.rest}")
    private String siteUrl;

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

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserRegisterDto userRegisterDto) {
        UserDto byEmail = userService.findByEmail(userRegisterDto.getEmail());
        if (byEmail != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        userService.save(userRegisterDto);
        return ResponseEntity.ok(UserMapper.userRegisterDtoToUerDto(userRegisterDto));
    }

    @GetMapping()
    public ResponseEntity<UserDto> getUserWithAddress(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(userService.findByIdWithAddresses(currentUser.getUser().getId()));
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<UserDto> updatePassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto,
                                                  @AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(userService.updatePassword(currentUser.getUser(), updatePasswordDto));
    }

    @PutMapping("/updateUserData")
    public ResponseEntity<UserDto> updateCurrentUserData(@Valid @RequestBody UserUpdateDto userUpdateDto,
                                                         @AuthenticationPrincipal CurrentUser currentUser, @RequestParam("profile_pic") MultipartFile multipartFile) throws IOException {
        return ResponseEntity.ok(userService.updateUser(multipartFile,userUpdateDto,currentUser.getUser()));
    }

    @PostMapping("/updateUserData/image")
    public ResponseEntity<UserDto> updateCurrentUserImage(@AuthenticationPrincipal CurrentUser currentUser,
                                                          @RequestParam("profile_pic") MultipartFile multipartFile) throws IOException {
        UserDto body = userService.updateUser(multipartFile, currentUser.getUser());
        body.setProfilePic(siteUrl + "/getImage?profilePic=" + body.getProfilePic());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<NotificationDto>> getUserAllNotifications(@PathVariable("userId") int id) {
        return ResponseEntity.ok(notificationService.findAllByUserId(id));
    }

    @GetMapping("/order")
    public ResponseEntity<List<OrderDto>> getUserOrders(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(orderService.findAllByUserId(currentUser.getUser().getId()));
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam("email") String email,
                                         @RequestParam("token") UUID token) {
        if (userService.verifyUserByEmail(email, token)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        UserDto userByEmail = userService.findByEmail(email);
        if (userByEmail != null) {
            mailService.sendMail(userByEmail.getEmail(), "Welcome",
                    "Hi " + userByEmail.getName() +
                            " Welcome please for change password by click " + siteUrl + "/user/changePassword?email=" + userByEmail.getEmail() + "&token=" + userByEmail.getToken()
            );
            return ResponseEntity.ok(userByEmail.getToken());
        }
        return ResponseEntity.notFound().build();
    }

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

    @PostMapping("/address")
    public ResponseEntity<UserDto> addUserAddress(@AuthenticationPrincipal CurrentUser currentUser,
                                                  @RequestBody AddressDto addressDto) {
        return ResponseEntity.ok(userService.saveAddress(currentUser.getUser(), addressDto));
    }

    @DeleteMapping("/address/delete")
    public ResponseEntity<UserDto> deleteUserAddress(@AuthenticationPrincipal CurrentUser currentUser,
                                                     @RequestParam("id") int id) {
        return ResponseEntity.ok(userService.removeAddressFromUserAndAddressTable(currentUser.getUser(), id));
    }

}
