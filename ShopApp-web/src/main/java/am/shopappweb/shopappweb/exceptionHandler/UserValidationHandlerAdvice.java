package am.shopappweb.shopappweb.exceptionHandler;

import am.shopappweb.shopappweb.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.userDto.UpdatePasswordDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserRegisterDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserUpdateDto;
import am.shoppingCommon.shoppingApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class UserValidationHandlerAdvice {
    private final UserService userService;

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
            modelAndView.setViewName("singleUserPage");

        } else if (target instanceof UpdatePasswordDto) {
            modelAndView.addObject("userUpdateDto", new UserUpdateDto());
            modelAndView.addObject("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
            modelAndView.setViewName("singleUserPage");
        } else if (target instanceof UserRegisterDto) {
            modelAndView.addObject("userRegisterDto", new UserRegisterDto());
            modelAndView.setViewName("register");
        }
        modelAndView.addObject(toLowerCase(errors.getTarget().getClass().getSimpleName()), errors.getTarget());
        return modelAndView;
    }


    private String toLowerCase(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

}
