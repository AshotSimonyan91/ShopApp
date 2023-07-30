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

@ControllerAdvice
@RequiredArgsConstructor
public class CategoryExceptionHandlerAdvice {
    private final CategoryService categoryService;
    private final UserService userService;
    private final NotificationService notificationService;

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
