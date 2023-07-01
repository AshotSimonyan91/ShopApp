package am.shopappRest.shoppingApplicationRest.endpoint;


import am.shopappRest.shoppingApplicationRest.restDto.userAuthDto.UserAuthRequestDto;
import am.shopappRest.shoppingApplicationRest.restDto.userAuthDto.UserAuthResponseDto;
import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shopappRest.shoppingApplicationRest.util.JwtTokenUtil;
import am.shoppingCommon.shoppingApplication.dto.addressDto.AddressDto;
import am.shoppingCommon.shoppingApplication.dto.notificationDto.NotificationResponseDto;
import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.*;
import am.shoppingCommon.shoppingApplication.entity.Role;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
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

    @Value("${site.url}")
    private String siteUrl;

    @PostMapping("/auth")
    public ResponseEntity<UserAuthResponseDto> auth(@RequestBody UserAuthRequestDto userAuthRequestDto) {
        User user = userService.findByEmail(userAuthRequestDto.getEmail());
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
    public ResponseEntity<UserShortDto> register(@RequestBody UserRegisterDto userRegisterDto) {
        User byEmail = userService.findByEmail(userRegisterDto.getEmail());
        if (byEmail != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        User user = UserMapper.userRegisterDtoToUser(userRegisterDto);
        user.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        user.setRole(Role.USER);
        userService.save(user);
        return ResponseEntity.ok(UserMapper.userToUserShortDto(user));
    }

    @GetMapping()
    public ResponseEntity<UserDto> getUserWithAddress(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(UserMapper.userToUserDto(userService.findByIdWithAddresses(currentUser.getUser().getId())));
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto,
                                            @AuthenticationPrincipal CurrentUser currentUser) {
        userService.updatePassword(currentUser.getUser(), updatePasswordDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/updateUserData")
    public ResponseEntity<UserShortDto> updateCurrentUserData(@Valid @RequestBody UserUpdateDto userUpdateDto,
                                                              @AuthenticationPrincipal CurrentUser currentUser){
        userService.updateUser(UserMapper.userUpdateDtoToUser(userUpdateDto), currentUser.getUser());
        return ResponseEntity.ok(UserMapper.userToUserShortDto(userService.findById(currentUser.getUser().getId())));
    }

    @PostMapping("/updateUserData/image")
    public ResponseEntity<UserShortDto> updateCurrentUserImage(@AuthenticationPrincipal CurrentUser currentUser,
                                                              @RequestParam("profile_pic") MultipartFile multipartFile) throws IOException {
        userService.updateUser(multipartFile, currentUser.getUser());
        return ResponseEntity.ok(UserMapper.userToUserShortDto(userService.findById(currentUser.getUser().getId())));
    }

    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<NotificationResponseDto>> getUserAllNotifications(@PathVariable("userId") int id) {
        return ResponseEntity.ok(NotificationMapper.map(notificationService.findAllByUserId(id)));
    }

    @GetMapping("/order")
    public ResponseEntity<List<OrderDto>> getUserOrders(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(OrderMapper.listOrderToListOrderDto(orderService.findAllByUserId(currentUser.getUser().getId())));
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
        User userByEmail = userService.findByEmail(email);
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
        userService.save(userService.saveAddress(currentUser.getUser(), addressDto));
        return ResponseEntity.ok(UserMapper.userToUserDto(userService.findByIdWithAddresses(currentUser.getUser().getId())));
    }

    @DeleteMapping("/address/delete")
    public ResponseEntity<UserDto> deleteUserAddress(@AuthenticationPrincipal CurrentUser currentUser,
                                                     @RequestParam("id") int id) {
        userService.removeAddressFromUserAndAddressTable(currentUser.getUser(), id);
        return ResponseEntity.ok(UserMapper.userToUserDto(userService.findByIdWithAddresses(currentUser.getUser().getId())));
    }

}
