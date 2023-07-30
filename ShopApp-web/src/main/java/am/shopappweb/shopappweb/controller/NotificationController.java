package am.shopappweb.shopappweb.controller;

import am.shopappweb.shopappweb.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.notificationDto.NotificationRequestDto;
import am.shoppingCommon.shoppingApplication.service.NotificationService;
import am.shoppingCommon.shoppingApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

/**
 * The NotificationController class handles HTTP requests related to notifications.
 * It provides functionality to send, remove, and view notifications for the authenticated user.
 * The controller interacts with the NotificationService and UserService to perform business logic.
 */
@Controller
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;


    /**
     * Handles the HTTP GET request to the send notification page ("/notification/send").
     * Populates the modelMap with user information and last three notifications for display
     * in the view.
     *
     * @param modelMap    The ModelMap to be populated with user information and notifications.
     * @param currentUser The currently authenticated user containing user information.
     * @return The view name "admin/send-notification" to display the send notification page.
     */
    @GetMapping("/send")
    public String sendNotificationPage(ModelMap modelMap, @AuthenticationPrincipal CurrentUser currentUser) {
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute("notifications", notificationService.last3Notifications(currentUser.getUser().getId()));
        return "admin/send-notification";
    }


    /**
     * Handles the HTTP POST request to send a notification.
     * Saves the notification using the NotificationService and redirects back to the send
     * notification page ("/notification/send").
     *
     * @param notificationRequestDto The NotificationRequestDto containing notification details.
     * @return A redirection to the send notification page.
     */
    @PostMapping("/send")
    public String sendNotification(@ModelAttribute NotificationRequestDto notificationRequestDto) {
        notificationService.save(notificationRequestDto);
        return "redirect:/notification/send";
    }


    /**
     * Handles the HTTP GET request to remove a notification.
     * Removes the notification with the specified ID using the NotificationService and
     * redirects back to the send notification page ("/notification/send").
     *
     * @param id The ID of the notification to be removed.
     * @return A redirection to the send notification page.
     */
    @GetMapping("/remove")
    public String removeNotification(@RequestParam("id") int id) {
        notificationService.remove(id);
        return "redirect:/notification/send";
    }

    /**
     * Handles the HTTP GET request to view notifications ("/notification").
     * Populates the modelMap with user information and all notifications for display in the view.
     *
     * @param currentUser The currently authenticated user containing user information.
     * @param modelMap    The ModelMap to be populated with user information and notifications.
     * @return The view name "notifications" to display the notifications page.
     */
    @GetMapping
    public String notificationsPage(@AuthenticationPrincipal CurrentUser currentUser, ModelMap modelMap) {
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute("notifications", notificationService.notifications(currentUser.getUser().getId()));
        return "notifications";
    }
}
