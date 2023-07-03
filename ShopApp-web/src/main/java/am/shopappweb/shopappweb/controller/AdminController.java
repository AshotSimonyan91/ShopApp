package am.shopappweb.shopappweb.controller;

import am.shopappweb.shopappweb.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.mapper.CategoryMapper;
import am.shoppingCommon.shoppingApplication.mapper.UserMapper;
import am.shoppingCommon.shoppingApplication.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final OrderService orderService;
    private final CategoryService categoryService;
    private final NotificationService notificationService;
    private final AdminService adminService;

    @GetMapping
    public String adminPage(ModelMap modelMap,
                            @AuthenticationPrincipal CurrentUser currentUser) {
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute("orders", orderService.ordersLimit10());
        modelMap.addAttribute("notifications", notificationService.last3Notifications(currentUser.getUser().getId()));
        return "/admin/admin-page";
    }

    @GetMapping("/remove")
    public String removeUser(@RequestParam("id") int id) {
        userService.removeById(id);
        return "redirect:/admin/all";
    }

    @GetMapping("/update")
    public String updateUserPage(@RequestParam("id") int id,
                                 ModelMap modelMap) {
        modelMap.addAttribute("userId", id);
        modelMap.addAttribute("user", UserMapper.userToUserDto(userService.findById(id)));
        return "admin/user-update";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute UserDto userDto, @RequestParam("img") MultipartFile profilePic) throws IOException {
        adminService.updateUser(userDto,profilePic);
        return "redirect:/admin";
    }


    @GetMapping("/add/product")
    public String addProductAdminPage(ModelMap modelMap, @AuthenticationPrincipal CurrentUser currentUser) {
        modelMap.addAttribute("categories", CategoryMapper.categoryDtoList(categoryService.findAllCategory()));
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute("notifications", notificationService.last3Notifications(currentUser.getUser().getId()));
        return "admin/add-product";
    }
    @GetMapping("/add/category")
    public String addCategoryAdminPage(ModelMap modelMap, @AuthenticationPrincipal CurrentUser currentUser) {
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute("notifications", notificationService.last3Notifications(currentUser.getUser().getId()));
        return "admin/add-category";
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
        adminService.editOrder(orderDto, deliveryId, currentUser.getUser());
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

    @GetMapping("/users")
    public String adminUsersPage(@AuthenticationPrincipal CurrentUser currentUser, ModelMap modelMap, @RequestParam("page") Optional<Integer> page,
                                 @RequestParam("size") Optional<Integer> size) {
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute("notifications", notificationService.last3Notifications(currentUser.getUser().getId()));
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(9);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        Page<User> result = userService.findAll(pageable);
        int totalPages = result.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            modelMap.addAttribute("pageNumbers", pageNumbers);
        }
        modelMap.addAttribute("totalPages", totalPages);
        modelMap.addAttribute("currentPage", currentPage);
        modelMap.addAttribute("users", result);
        return "/admin/user-list";
    }


    @GetMapping("/block/{id}")
    public String blockUser(@PathVariable("id") int id, @AuthenticationPrincipal CurrentUser currentUser) {
        adminService.block(id, currentUser.getUser());
        return "redirect:/admin/users";

    }

    @GetMapping("/unblock/{id}")
    public String unblockUser(@PathVariable("id") int id, @AuthenticationPrincipal CurrentUser currentUser) {
        adminService.unBlock(id, currentUser.getUser());
        return "redirect:/admin/users";
    }
}
