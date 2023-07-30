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

/**
 * This controller advice class handles exceptions related to email operations, such as EmailAlreadyExistsException.
 * It provides methods to handle specific exceptions and return appropriate views with error messages.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class EmailExceptionHandlerAdvice {
    private final UserService userService;

    /**
     * Handles the EmailAlreadyExistsException and returns a ModelAndView with an error message for email already in use.
     * The method populates the ModelAndView with the necessary attributes, such as error message, user update DTO,
     * update password DTO, and user details to display the "singleUserPage" view with the appropriate error message.
     *
     * @param currentUser The currently authenticated user (CurrentUser).
     * @return A ModelAndView with the "singleUserPage" view, displaying an error message for email already in use.
     */

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

    /**
     * Handles the NullPointerException and returns a ModelAndView with a generic error view for handling null pointer exceptions.
     * The method returns the "admin/error-404-basic" view to display a basic 404 error page for unexpected errors.
     *
     * @param currentUser The currently authenticated user (CurrentUser).
     * @return A ModelAndView with the "admin/error-404-basic" view, displaying a generic error page for unexpected errors.
     */
    @ExceptionHandler(NullPointerException.class)
    public ModelAndView allExceptionHandler(@AuthenticationPrincipal CurrentUser currentUser) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/admin/error-404-basic");
        return modelAndView;
    }

}
