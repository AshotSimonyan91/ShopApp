package am.shopappRest.shoppingApplicationRest.endpoint;


import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.cartDto.CartItemDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UpdatePasswordDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserShortDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserUpdateDto;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.mapper.UserMapper;
import am.shoppingCommon.shoppingApplication.service.CartService;
import am.shoppingCommon.shoppingApplication.service.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@ControllerAdvice
@RequiredArgsConstructor
public class CurrentUserAdviceEndpoint {
    private final CartService cartService;
    private final UserService userService;

    @GetMapping("/currentUser")
    public ResponseEntity<UserShortDto> getCurrentUser(@AuthenticationPrincipal CurrentUser currentUser) {
        if (currentUser != null) {
            User user = currentUser.getUser();
            UserShortDto userShortDto = UserMapper.userToUserShortDto(user);
            return ResponseEntity.ok(userShortDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ModelAndView handleIllegalArgument(MethodArgumentNotValidException methodArgumentNotValidException,
                                              @AuthenticationPrincipal CurrentUser currentUser,
                                              BindingResult errors) {
        ModelAndView modelAndView = new ModelAndView();
        List<FieldError> fieldErrors = methodArgumentNotValidException.getBindingResult().getFieldErrors();

        for (FieldError fieldError : fieldErrors) {
            modelAndView.addObject(fieldError.getField() + "_", fieldError.getDefaultMessage());
        }
        Object target = errors.getTarget();
        if (target instanceof UserUpdateDto) {
            modelAndView.addObject("updatePasswordDto", new UpdatePasswordDto());
            modelAndView.addObject("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        } else {
            modelAndView.addObject("userUpdateDto", new UserUpdateDto());
            modelAndView.addObject("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        }
        modelAndView.addObject(toLowerCase(errors.getTarget().getClass().getSimpleName()), errors.getTarget());
        return modelAndView;
    }

    private String toLowerCase(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    @ModelAttribute("cartItems")
    public ResponseEntity<List<CartItemDto>> currentUserCart(@AuthenticationPrincipal CurrentUser currentUser) {
        if (currentUser != null) {
            return ResponseEntity.ok(cartService.findLastCartItemsByLimit(currentUser.getUser().getId()));
        }
        return ResponseEntity.notFound().build();
    }
}
