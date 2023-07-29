package am.shopappweb.shopappweb.exceptionHandler;

import am.shopappweb.shopappweb.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.userDto.UpdatePasswordDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserUpdateDto;
import am.shoppingCommon.shoppingApplication.exception.EmailAlreadyExistsException;
import am.shoppingCommon.shoppingApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@RequiredArgsConstructor
public class EmailExceptionHandlerAdvice {
    private final UserService userService;
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ModelAndView handleEmailAlreadyExistsException(@AuthenticationPrincipal CurrentUser currentUser) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("emailInvalid", "Email is already used");
        modelAndView.addObject("userUpdateDto", new UserUpdateDto());
        modelAndView.addObject("updatePasswordDto", new UpdatePasswordDto());
        modelAndView.addObject("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelAndView.setViewName("singleUserPage");
        return modelAndView;
    }
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGlobalException(Exception ex) {
        ModelAndView modelAndView = new ModelAndView("error"); // Specify the error view template
        modelAndView.addObject("errorMessage", "An error occurred. Please try again later.");
        return modelAndView;
    }
}
