package am.shopappweb.shopappweb.controller;

import am.shoppingCommon.shoppingApplication.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.service.CategoryService;
import am.shoppingCommon.shoppingApplication.service.OrderService;
import am.shoppingCommon.shoppingApplication.service.UserService;
import am.shoppingCommon.shoppingApplication.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final OrderService orderService;
    private final CategoryService categoryService;

    @GetMapping
    public String adminPage(ModelMap modelMap,
                            @AuthenticationPrincipal CurrentUser currentUser) {
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute("orders", orderService.ordersLimit10());
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
    public String addProductAdminPage(ModelMap modelMap) {
        modelMap.addAttribute("categories", categoryService.findAllCategory());
        return "admin/form-uploads";
    }

}
