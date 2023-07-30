package am.shopappweb.shopappweb.controller;


import am.shopappweb.shopappweb.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.entity.Status;
import am.shoppingCommon.shoppingApplication.service.OrderService;
import am.shoppingCommon.shoppingApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The OrderController class handles HTTP requests related to orders and the checkout process.
 * It allows users to view their orders, add new orders, and remove products from existing orders.
 * The controller interacts with the OrderService and UserService to perform business logic.
 */
@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;


    /**
     * Handles the HTTP GET request to view the checkout page ("/order").
     * Populates the modelMap with user information and pending orders for display in the view.
     * If the user does not have any addresses saved, they will be redirected to the address page.
     *
     * @param modelMap    The ModelMap to be populated with user information and orders.
     * @param currentUser The currently authenticated user containing user information.
     * @return The view name "checkout" to display the checkout page.
     */
    @GetMapping
    public String orderPage(ModelMap modelMap,
                            @AuthenticationPrincipal CurrentUser currentUser) {
        UserDto byId = userService.findById(currentUser.getUser().getId());
        if (byId.getAddresses().size() == 0) {
            return "redirect:/user/address";
        }
        modelMap.addAttribute("order", orderService
                .findByUserIdAndStatus(currentUser.getUser().getId(), Status.PENDING));
        modelMap.addAttribute("user", byId);
        return "checkout";
    }


    /**
     * Handles the HTTP POST request to add a new order.
     * Saves a new order for the currently authenticated user using the OrderService
     * and redirects back to the cart page ("/cart").
     *
     * @param currentUser The currently authenticated user containing user information.
     * @return A redirection to the cart page.
     */
    @PostMapping("/add")
    public String addOrder(@AuthenticationPrincipal CurrentUser currentUser) {
        orderService.save(currentUser.getUser().getId());
        return "redirect:/cart";
    }


    /**
     * Handles the HTTP GET request to remove a product from an existing order.
     * Removes the specified product from the order of the currently authenticated user
     * using the OrderService and redirects back to the order page ("/order").
     *
     * @param product_id   The ID of the product to be removed from the order.
     * @param orderItem_id The ID of the order item corresponding to the product in the order.
     * @param currentUser  The currently authenticated user containing user information.
     * @return A redirection to the order page.
     */
    @GetMapping("/remove")
    public String removeProductFromOrder(@RequestParam("product_id") int product_id,
                                         @RequestParam("orderItem_id") int orderItem_id,
                                         @AuthenticationPrincipal CurrentUser currentUser) {
        orderService.removeByProductIdAndOrderItemId(product_id, orderItem_id, currentUser.getUser().getId());
        return "redirect:/order";
    }
}
