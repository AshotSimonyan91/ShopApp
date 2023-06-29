package am.shopappweb.shopappweb.controller;

import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.mapper.CategoryMapper;
import am.shopappweb.shopappweb.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.service.*;
import am.shoppingCommon.shoppingApplication.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final OrderService orderService;
    private final CategoryService categoryService;
    private final NotificationService notificationService;
    private final AdminService adminService;
    private final DeliveryService deliveryService;

    @GetMapping
    public String adminPage(ModelMap modelMap,
                            @AuthenticationPrincipal CurrentUser currentUser) {
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute("orders", orderService.ordersLimit10());
        modelMap.addAttribute("notifications", notificationService.last3Notifications(currentUser.getUser().getId()));
        return "/admin/admin-page";
    }

    @GetMapping("remove")
    public String removeUser(@RequestParam("id") int id) {
        userService.removeById(id);
        return "redirect:/admin/all";
    }

    @GetMapping("update")
    public String updateUserPage(@RequestParam("id") int id,
                                 ModelMap modelMap) {
        modelMap.addAttribute("user", UserMapper.userToUserDto(userService.findById(id)));
        return "updateUser";
    }

    @GetMapping("/all")
    public String allUsersPage(ModelMap modelMap,
                               @AuthenticationPrincipal CurrentUser currentUser) {
        modelMap.addAttribute("currentUser", UserMapper.userToUserDto(currentUser.getUser()));
        modelMap.addAttribute("users", UserMapper.userDtoListMap(userService.findAll()));
        return "allUsers";
    }

    @GetMapping("/add/product")
    public String addProductAdminPage(ModelMap modelMap, @AuthenticationPrincipal CurrentUser currentUser) {
        modelMap.addAttribute("categories", CategoryMapper.categoryDtoList(categoryService.findAllCategory()));
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute("notifications", notificationService.last3Notifications(currentUser.getUser().getId()));
        return "admin/add-product";
    }

    @GetMapping("/block/{id}")
    public String blockUser(@PathVariable("id") int id, @AuthenticationPrincipal CurrentUser currentUser) {
        adminService.block(id, currentUser.getUser());
        return "redirect:/admin";
    }

    @GetMapping("/edit/order/{id}")
    public String editOrderPage(@PathVariable("id") int id, @AuthenticationPrincipal CurrentUser currentUser, ModelMap modelMap) {
        modelMap.addAttribute("user", UserMapper.userToUserDto(userService.findByIdWithAddresses(currentUser.getUser().getId())));
        modelMap.addAttribute("order", orderService.orderById(id));
        modelMap.addAttribute("deliveries", userService.findAllDeliveries());
        return "admin/order-edit";
    }

    @PostMapping("/edit/order")
    public String editOrder(@ModelAttribute OrderDto orderDto, @RequestParam("delivery") int deliveryId, @AuthenticationPrincipal CurrentUser currentUser) {
        adminService.editOrder(orderDto, currentUser.getUser());
        return "redirect:/admin";
    }

    @GetMapping("/remove/orderItem/{productId}/{orderItemId}/{userId}/{orderId}")
    public String removeOrderItemAdmin(@PathVariable("productId") int productId,
                                       @PathVariable("orderItemId") int orderItemId,
                                       @PathVariable("userId") int userId,
                                       @PathVariable("orderId") int orderId) {
        orderService.removeByProductIdAndOrderItemId(productId, orderItemId, userId);
        return "redirect:/admin/edit/order/" + orderId;
    }

}
