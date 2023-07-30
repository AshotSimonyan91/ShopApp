package am.shopappweb.shopappweb.controller;

import am.shopappweb.shopappweb.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.dto.productDto.CreateProductRequestDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
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

/**
 * This controller handles various administrative functionalities and serves the corresponding admin-related views.
 * It is responsible for managing users, orders, products, categories, and handling user blocking/unblocking.
 */

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final OrderService orderService;
    private final CategoryService categoryService;
    private final NotificationService notificationService;
    private final AdminService adminService;
    private final ProductService productService;

    /**
     * Handles the request to the admin main page and populates the model with necessary data for rendering the view.
     *
     * @param modelMap    The ModelMap to store attributes to be used in the view.
     * @param currentUser The currently authenticated user.
     * @return The name of the view to be rendered (admin/admin-page).
     */
    @GetMapping
    public String adminPage(ModelMap modelMap,
                            @AuthenticationPrincipal CurrentUser currentUser) {
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute("orders", orderService.ordersLimit10());
        modelMap.addAttribute("notifications", notificationService.last3Notifications(currentUser.getUser().getId()));
        return "/admin/admin-page";
    }

    /**
     * Handles the request to remove a user with the specified ID and redirects to the admin users' list page.
     *
     * @param id The ID of the user to be removed.
     * @return The redirect URL to the admin users' list page.
     */
    @GetMapping("/remove")
    public String removeUser(@RequestParam("id") int id) {
        userService.removeById(id);
        return "redirect:/admin/all";
    }

    /**
     * Handles the request to the user update page and populates the model with user data.
     *
     * @param id       The ID of the user to be updated.
     * @param modelMap The ModelMap to store attributes to be used in the view.
     * @return The name of the view to be rendered (admin/user-update).
     */

    @GetMapping("/update")
    public String updateUserPage(@RequestParam("id") int id,
                                 ModelMap modelMap) {
        modelMap.addAttribute("userId", id);
        modelMap.addAttribute("user", userService.findById(id));
        return "admin/user-update";
    }

    /**
     * Handles the request to update a user's information with the provided UserDto and profile picture.
     *
     * @param userDto     The UserDto containing the updated user information.
     * @param profilePic  The profile picture file to be uploaded.
     * @return The redirect URL to the admin main page.
     * @throws IOException If an I/O error occurs during profile picture processing.
     */
    @PostMapping("/update")
    public String updateUser(@ModelAttribute UserDto userDto, @RequestParam("img") MultipartFile profilePic) throws IOException {
        adminService.updateUser(userDto, profilePic);
        return "redirect:/admin";
    }

    /**
     * Handles the request to the add product admin page and populates the model with necessary data for rendering the view.
     *
     * @param modelMap    The ModelMap to store attributes to be used in the view.
     * @param currentUser The currently authenticated user.
     * @return The name of the view to be rendered (admin/add-product).
     */
    @GetMapping("/add/product")
    public String addProductAdminPage(ModelMap modelMap, @AuthenticationPrincipal CurrentUser currentUser) {
        modelMap.addAttribute("categories", categoryService.findAllCategory());
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute("notifications", notificationService.last3Notifications(currentUser.getUser().getId()));
        return "admin/add-product";
    }

    /**
     * Handles the request to the edit product admin page and populates the model with necessary data for rendering the view.
     *
     * @param modelMap    The ModelMap to store attributes to be used in the view.
     * @param id          The ID of the product to be edited.
     * @param currentUser The currently authenticated user.
     * @return The name of the view to be rendered (admin/edit-product).
     */
    @GetMapping("/edit/product")
    public String editProductAdminPage(ModelMap modelMap, @RequestParam("id") int id, @AuthenticationPrincipal CurrentUser currentUser) {
        modelMap.addAttribute("productId", id);
        modelMap.addAttribute("categories", categoryService.findAllCategory());
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute("notifications", notificationService.last3Notifications(currentUser.getUser().getId()));
        return "admin/edit-product";
    }

    /**
     * Handles the request to add a new product or edit an existing product with the provided CreateProductRequestDto
     * and images, and associates the changes with the currently authenticated user.
     *
     * @param createProductRequestDto The CreateProductRequestDto containing the product information.
     * @param currentUser             The currently authenticated user.
     * @param files                   The array of image files to be uploaded.
     * @param id                      The ID of the product (if editing an existing product).
     * @return The redirect URL to the admin main page.
     * @throws IOException If an I/O error occurs during image processing.
     */

    @PostMapping("/edit/product")
    public String addProduct(@ModelAttribute CreateProductRequestDto createProductRequestDto,
                             @AuthenticationPrincipal CurrentUser currentUser,
                             @RequestParam("files") MultipartFile[] files,@RequestParam("id") int id) throws IOException {
        adminService.editProduct(createProductRequestDto, files, currentUser.getUser(),id);
        return "redirect:/admin";
    }
    /**
     * Handles the request to remove a product with the specified ID and redirects to the admin main page.
     *
     * @param id           The ID of the product to be removed.
     * @param currentUser  The currently authenticated user.
     * @return The redirect URL to the admin main page.
     *//**
     * Handles the request to remove a product with the specified ID and redirects to the admin main page.
     *
     * @param id           The ID of the product to be removed.
     * @param currentUser  The currently authenticated user.
     * @return The redirect URL to the admin main page.
     */
    @GetMapping("/remove/product")
    public String removeProduct(@RequestParam ("id") int id,@AuthenticationPrincipal CurrentUser currentUser){
        productService.remove(id,currentUser.getUser());
        return "redirect:/admin";
    }
    /**
     * Handles the request to the add category admin page and populates the model with necessary data for rendering the view.
     *
     * @param modelMap    The ModelMap to store attributes to be used in the view.
     * @param currentUser The currently authenticated user.
     * @return The name of the view to be rendered (admin/add-category).
     */

    @GetMapping("/add/category")
    public String addCategoryAdminPage(ModelMap modelMap, @AuthenticationPrincipal CurrentUser currentUser) {
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute("notifications", notificationService.last3Notifications(currentUser.getUser().getId()));
        return "admin/add-category";
    }
    /**
     * Handles the request to the edit order page and populates the model with necessary data for rendering the view.
     *
     * @param id          The ID of the order to be edited.
     * @param currentUser The currently authenticated user.
     * @param modelMap    The ModelMap to store attributes to be used in the view.
     * @return The name of the view to be rendered (admin/order-edit).
     */
    @GetMapping("/edit/order/{id}")
    public String editOrderPage(@PathVariable("id") int id, @AuthenticationPrincipal CurrentUser currentUser, ModelMap modelMap) {
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute("order", orderService.orderById(id));
        modelMap.addAttribute("deliveries", userService.findAllDeliveries());
        return "admin/order-edit";
    }
    /**
     * Handles the request to edit an order with the provided OrderDto and delivery ID,
     * and associates the changes with the currently authenticated user.
     *
     * @param orderDto    The OrderDto containing the updated order information.
     * @param deliveryId  The ID of the selected delivery option for the order.
     * @param currentUser The currently authenticated user.
     * @return The redirect URL to the admin main page.
     */

    @PostMapping("/edit/order")
    public String editOrder(@ModelAttribute OrderDto orderDto, @RequestParam("delivery") int deliveryId, @AuthenticationPrincipal CurrentUser currentUser) {
        adminService.editOrder(orderDto, deliveryId, currentUser.getUser());
        return "redirect:/admin";
    }
    /**
     * Handles the request to remove an order item from the order with the specified IDs and redirects to the order edit page.
     *
     * @param productId   The ID of the product associated with the order item to be removed.
     * @param orderItemId The ID of the order item to be removed.
     * @param userId      The ID of the user associated with the order.
     * @param orderId     The ID of the order from which the item will be removed.
     * @return The redirect URL to the order edit page.
     */
    @GetMapping("/remove/orderItem/{productId}/{orderItemId}/{userId}/{orderId}")
    public String removeOrderItemAdmin(@PathVariable("productId") int productId,
                                       @PathVariable("orderItemId") int orderItemId,
                                       @PathVariable("userId") int userId,
                                       @PathVariable("orderId") int orderId) {
        orderService.removeByProductIdAndOrderItemId(productId, orderItemId, userId);
        return "redirect:/admin/edit/order/" + orderId;
    }

    /**
     * Handles the request to the admin users' list page and populates the model with necessary data for rendering the view.
     * Supports pagination for displaying users.
     *
     * @param currentUser The currently authenticated user.
     * @param modelMap    The ModelMap to store attributes to be used in the view.
     * @param page        The optional page number for pagination.
     * @param size        The optional page size for pagination.
     * @return The name of the view to be rendered (admin/user-list).
     */

    @GetMapping("/users")
    public String adminUsersPage(@AuthenticationPrincipal CurrentUser currentUser, ModelMap modelMap, @RequestParam("page") Optional<Integer> page,
                                 @RequestParam("size") Optional<Integer> size) {
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute("notifications", notificationService.last3Notifications(currentUser.getUser().getId()));
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(9);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        Page<UserDto> result = userService.findAll(pageable);
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
    /**
     * Handles the request to block a user with the specified ID and redirects to the admin users' list page.
     *
     * @param id           The ID of the user to be blocked.
     * @param currentUser  The currently authenticated user.
     * @return The redirect URL to the admin users' list page.
     */
    @GetMapping("/block/{id}")
    public String blockUser(@PathVariable("id") int id, @AuthenticationPrincipal CurrentUser currentUser) {
        adminService.block(id, currentUser.getUser());
        return "redirect:/admin/users";

    }
    /**
     * Handles the request to unblock a user with the specified ID and redirects to the admin users' list page.
     *
     * @param id           The ID of the user to be unblocked.
     * @param currentUser  The currently authenticated user.
     * @return The redirect URL to the admin users' list page.
     */
    @GetMapping("/unblock/{id}")
    public String unblockUser(@PathVariable("id") int id, @AuthenticationPrincipal CurrentUser currentUser) {
        adminService.unBlock(id, currentUser.getUser());
        return "redirect:/admin/users";
    }
}
