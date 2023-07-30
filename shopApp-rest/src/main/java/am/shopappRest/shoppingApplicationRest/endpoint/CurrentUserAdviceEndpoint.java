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

/**
 * This advice endpoint class provides global advice for handling current user-related operations and exceptions.
 * It is responsible for providing information about the currently authenticated user, handling validation errors,
 * and populating the current user's cart items. The class uses Spring's @ControllerAdvice annotation to handle global
 * aspects across multiple controllers.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class CurrentUserAdviceEndpoint {
    private final CartService cartService;
    private final UserService userService;

    /**
     * GET endpoint that returns information about the currently authenticated user. If a user is authenticated, the method
     * retrieves the user details and converts them into a UserShortDto containing basic user information. It then returns a
     * ResponseEntity with the UserShortDto as the response body. If no user is authenticated, it returns a ResponseEntity
     * with a "not found" status.
     *
     * @param currentUser The CurrentUser object representing the currently authenticated user.
     * @return ResponseEntity containing the UserShortDto with basic user information if authenticated, or "not found" status if not.
     */
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

    /**
     * Exception handler method that handles MethodArgumentNotValidException, which occurs when there are validation errors in the request.
     * The method extracts the validation errors from the exception and populates a ModelAndView with error messages. It also adds
     * the appropriate user update DTO (UserUpdateDto or UpdatePasswordDto) and the user's information to the model to be displayed
     * in the UI. The method returns the populated ModelAndView to handle the validation errors in the view.
     *
     * @param methodArgumentNotValidException The MethodArgumentNotValidException containing validation errors.
     * @param currentUser                     The CurrentUser object representing the currently authenticated user.
     * @param errors                         The BindingResult object containing the validation errors.
     * @return ModelAndView containing error messages and user information for the UI.
     */
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

    /**
     * A utility method to convert the first letter of a given string to lowercase.
     * @param str The input string to be converted.
     * @return The input string with the first letter converted to lowercase.
     */
    private String toLowerCase(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * Model attribute method that provides the current user's cart items as a ResponseEntity to be included in the model of views.
     * If a user is authenticated, the method retrieves the cart items for that user and returns them as a ResponseEntity with the
     * list of CartItemDto objects as the response body. If no user is authenticated, it returns a ResponseEntity with a "not found" status.
     *
     * @param currentUser The CurrentUser object representing the currently authenticated user.
     * @return ResponseEntity containing the list of CartItemDto objects with cart item information if authenticated, or "not found" status if not.
     */
    @ModelAttribute("cartItems")
    public ResponseEntity<List<CartItemDto>> currentUserCart(@AuthenticationPrincipal CurrentUser currentUser) {
        if (currentUser != null) {
            return ResponseEntity.ok(cartService.findLastCartItemsByLimit(currentUser.getUser().getId()));
        }
        return ResponseEntity.notFound().build();
    }
}
