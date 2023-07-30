package am.shopappweb.shopappweb.exceptionHandler;

import am.shopappweb.shopappweb.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.exception.CategoryDoesNotExistsException;
import am.shoppingCommon.shoppingApplication.service.CategoryService;
import am.shoppingCommon.shoppingApplication.service.NotificationService;
import am.shoppingCommon.shoppingApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
/**
 * This controller advice class handles exceptions related to categories.
 * It provides methods to handle specific exceptions and return appropriate views with error messages.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class CategoryExceptionHandlerAdvice {
    private final CategoryService categoryService;
    private final UserService userService;
    private final NotificationService notificationService;

    /**
     * Handles the CategoryDoesNotExistsException and returns a ModelAndView with an error message for adding a category.
     * The method populates the ModelAndView with the necessary attributes such as categories, user details, and notifications
     * to display the "admin/add-product" view with the appropriate error message.
     *
     * @param currentUser The currently authenticated user (CurrentUser).
     * @return A ModelAndView with the "admin/add-product" view, displaying an error message for adding a category.
     */

    @ExceptionHandler(CategoryDoesNotExistsException.class)
    public ModelAndView handleEmailAlreadyExistsException(@AuthenticationPrincipal CurrentUser currentUser) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("error", "Please add category");
        modelAndView.addObject("categories", categoryService.findAllCategory());
        modelAndView.addObject("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelAndView.addObject("notifications", notificationService.last3Notifications(currentUser.getUser().getId()));
        modelAndView.setViewName("admin/add-product");
        return modelAndView;
    }
}
