package am.shopappweb.shopappweb.controller;

import am.shopappweb.shopappweb.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.mapper.UserMapper;
import am.shoppingCommon.shoppingApplication.service.NotificationService;
import am.shoppingCommon.shoppingApplication.dto.notificationDto.NotificationRequestDto;
import am.shoppingCommon.shoppingApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping("/send")
    public String sendNotificationPage(ModelMap modelMap,@AuthenticationPrincipal CurrentUser currentUser) {
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute("notifications", notificationService.last3Notifications(currentUser.getUser().getId()));
        return "admin/send-notification";
    }

    @PostMapping("/send")
    public String sendNotification(@ModelAttribute NotificationRequestDto notificationRequestDto) {
        notificationService.save(notificationRequestDto);
        return "redirect:/notification/send";
    }

    @GetMapping("/remove")
    public String removeNotification(@RequestParam("id") int id) {
        notificationService.remove(id);
        return "redirect:/notification/send";
    }

    @GetMapping
    public String notificationsPage(@AuthenticationPrincipal CurrentUser currentUser, ModelMap modelMap) {
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute("notifications", notificationService.notifications(currentUser.getUser().getId()));
        return "notifications";
    }
}
