package am.shopappweb.shopappweb.controller;


import am.shopappweb.shopappweb.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserRegisterDto;
import am.shoppingCommon.shoppingApplication.service.CartService;
import am.shoppingCommon.shoppingApplication.service.UserService;
import am.shoppingCommon.shoppingApplication.dto.cartDto.CartItemDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UpdatePasswordDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserShortDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserUpdateDto;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class CurrentUserControllerAdvice {
    private final CartService cartService;

    private final UserService userService;

    @ModelAttribute("currentUser")
    public UserShortDto currentUser(@AuthenticationPrincipal CurrentUser currentUser) {
        if (currentUser != null) {
            User user = currentUser.getUser();
            return UserMapper.userToUserShortDto(user);
        }
        return null;
    }
      @ModelAttribute("cartItems")
    public List<CartItemDto> currentUserCart(@AuthenticationPrincipal CurrentUser currentUser) {
        if (currentUser != null) {
            return cartService.findLastCartItemsByLimit(currentUser.getUser().getId());
        }
        return null;
    }
}