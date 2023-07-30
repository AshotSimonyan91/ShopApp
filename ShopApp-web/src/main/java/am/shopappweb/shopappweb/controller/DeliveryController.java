package am.shopappweb.shopappweb.controller;


import am.shopappweb.shopappweb.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.deliveryDto.DeliveryDto;
import am.shoppingCommon.shoppingApplication.entity.Status;
import am.shoppingCommon.shoppingApplication.service.DeliveryService;
import am.shoppingCommon.shoppingApplication.service.OrderService;
import am.shoppingCommon.shoppingApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * This controller class handles the web requests related to deliveries and provides views for delivery-related pages.
 * It is responsible for managing deliveries, displaying delivery details, and listing deliveries for the current user.
 */
@Controller
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;
    private final UserService userService;
    private final OrderService orderService;

    /**
     * Handles the request to add a new delivery for the specified order ID.
     * The delivery is created by the DeliveryService, and the user is redirected to the homepage (/) after the operation.
     *
     * @param id The ID of the order for which the delivery is added.
     * @return The view name for redirecting to the homepage.
     */
    @GetMapping("/add")
    public String addDelivery(@RequestParam("order_id") int id) {
        deliveryService.save(id);
        return "redirect:/";
    }

    /**
     * Handles the request to remove a delivery with the specified ID and redirect the user back to the product page.
     * The delivery is removed by the DeliveryService, and the user is redirected to the product page with the specified product ID.
     *
     * @param id        The ID of the delivery to be removed.
     * @param productId The ID of the product for which the delivery is associated.
     * @return The view name for redirecting to the product page with the specified product ID.
     */

    @GetMapping("/remove")
    public String removeDelivery(@RequestParam("id") int id, @RequestParam("ProductId") int productId) {
        deliveryService.remove(id);
        return "redirect:/products" + productId;
    }

    /**
     * Handles the request to display the delivery details for a specific customer (user).
     * The user details are fetched from the UserService based on the provided user ID, and the view "singleDeliveryUserPage" is returned.
     *
     * @param modelmap The model map for passing attributes to the view.
     * @param id       The ID of the user for whom the delivery details are displayed.
     * @return The view name for displaying the delivery details of the specified user.
     */

    @GetMapping("/customer")
    public String deliveryUserPage(ModelMap modelmap,
                                   @RequestParam("id") int id) {
        modelmap.addAttribute("user", userService.findByIdWithAddresses(id));
        return "singleDeliveryUserPage";
    }
    /**
     * Handles the request to display the delivery details for a specific order.
     * The user details and order details are fetched from the UserService and OrderService respectively,
     * based on the provided order ID and the currently authenticated user's ID.
     * The view "singleDeliveryOrderPage" is returned with the user and order attributes.
     *
     * @param modelmap    The model map for passing attributes to the view.
     * @param currentUser The currently authenticated user (CurrentUser).
     * @param id          The ID of the order for which the delivery details are displayed.
     * @return The view name for displaying the delivery details of the specified order.
     */

    @GetMapping("/order")
    public String deliveryOrderPage(ModelMap modelmap,
                                    @AuthenticationPrincipal CurrentUser currentUser,
                                    @RequestParam("id") int id) {
        modelmap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelmap.addAttribute("order", orderService.orderById(id));
        return "singleDeliveryOrderPage";
    }

    /**
     * Handles the request to display the delivery page for the current user.
     * The page number and size for pagination are passed as optional parameters.
     * The delivery details for the current user are fetched from the DeliveryService and UserService,
     * and the paginated results are displayed using the "account-delivery" view.
     *
     * @param currentUser The currently authenticated user (CurrentUser).
     * @param page        The optional page number for pagination.
     * @param size        The optional page size for pagination.
     * @param modelMap    The model map for passing attributes to the view.
     * @return The view name for displaying the delivery page for the current user.
     */
    @GetMapping
    public String deliveryPage(@AuthenticationPrincipal CurrentUser currentUser,
                               @RequestParam("page") Optional<Integer> page,
                               @RequestParam("size") Optional<Integer> size,
                               ModelMap modelMap) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(9);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        Page<DeliveryDto> result = deliveryService.findAllByOrderStatus(Status.APPROVED, pageable);
        int totalPages = result.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .toList();
            modelMap.addAttribute("pageNumbers", pageNumbers);
        }
        modelMap.addAttribute("totalPages", totalPages);
        modelMap.addAttribute("currentPage", currentPage);
        modelMap.addAttribute("deliveries1", result);
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute("deliveries2",
                deliveryService.findAllByUserIdAndOrderStatus(currentUser.getUser().getId(), Status.IN_PROCESS, pageable).getContent());
        return "account-delivery";
    }

    /**
     * Handles the request to display the custom delivery page for the current user with a specific delivery status.
     * The user selects a delivery with the provided delivery ID and the delivery status is set to IN_PROCESS.
     * The delivery details for the current user are fetched from the DeliveryService and UserService,
     * and the paginated results are displayed using the "account-delivery" view.
     *
     * @param currentUser The currently authenticated user (CurrentUser).
     * @param id          The ID of the selected delivery for the IN_PROCESS status.
     * @param page        The optional page number for pagination.
     * @param size        The optional page size for pagination.
     * @param modelMap    The model map for passing attributes to the view.
     * @return The view name for displaying the custom delivery page for the current user with the IN_PROCESS status.
     */

    @GetMapping("/custom")
    public String customDeliveryPage(@AuthenticationPrincipal CurrentUser currentUser,
                                     @RequestParam("delivery_id") int id,
                                     @RequestParam("page") Optional<Integer> page,
                                     @RequestParam("size") Optional<Integer> size,
                                     ModelMap modelMap) {
        deliveryService.chooseDelivery(currentUser.getUser(), id, Status.IN_PROCESS);
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(9);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        Page<DeliveryDto> result = deliveryService.findAllByOrderStatus(Status.APPROVED, pageable);
        int totalPages = result.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .toList();
            modelMap.addAttribute("pageNumbers", pageNumbers);
        }
        modelMap.addAttribute("totalPages", totalPages);
        modelMap.addAttribute("currentPage", currentPage);
        modelMap.addAttribute("deliveries1", result);
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute("deliveries2",
                deliveryService.findAllByUserIdAndOrderStatus(currentUser.getUser().getId(), Status.IN_PROCESS, pageable).getContent());
        return "account-delivery";
    }
    /**
     * Handles the request to display the in-process delivery page for the current user.
     * The page number and size for pagination are passed as optional parameters.
     * The delivery details for the current user with the status IN_PROCESS are fetched from the DeliveryService,
     * and the paginated results are displayed using the "account-delivery-custom" view.
     *
     * @param currentUser The currently authenticated user (CurrentUser).
     * @param page        The optional page number for pagination.
     * @param size        The optional page size for pagination.
     * @param modelMap    The model map for passing attributes to the view.
     * @return The view name for displaying the in-process delivery page for the current user.
     */
    @GetMapping("/inProcess")
    public String inProcessDeliveryPage(@AuthenticationPrincipal CurrentUser currentUser,
                                        @RequestParam("page") Optional<Integer> page,
                                        @RequestParam("size") Optional<Integer> size,
                                        ModelMap modelMap) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(9);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        Page<DeliveryDto> result = deliveryService.findAllByUserIdAndOrderStatus(currentUser.getUser().getId(), Status.IN_PROCESS, pageable);
        int totalPages = result.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .toList();
            modelMap.addAttribute("pageNumbers", pageNumbers);
        }
        modelMap.addAttribute("totalPages", totalPages);
        modelMap.addAttribute("currentPage", currentPage);
        modelMap.addAttribute("deliveries2", result);
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute("deliveries1",
                deliveryService.findAllByOrderStatus(Status.APPROVED, pageable).getContent());
        return "account-delivery-custom";
    }

    /**
     * Handles the request to display the custom in-process delivery page for the current user with a specific delivery status.
     * The user selects a delivery with the provided delivery ID and the delivery status is set to DELIVERED.
     * The delivery details for the current user are fetched from the DeliveryService and UserService,
     * and the paginated results are displayed using the "account-delivery-custom" view.
     *
     * @param currentUser The currently authenticated user (CurrentUser).
     * @param id          The ID of the selected delivery for the DELIVERED status.
     * @param page        The optional page number for pagination.
     * @param size        The optional page size for pagination.
     * @param modelMap    The model map for passing attributes to the view.
     * @return The view name for displaying the custom in-process delivery page for the current user with the DELIVERED status.
     */
    @GetMapping("/inProcess/custom")
    public String customInProcessDeliveryPage(@AuthenticationPrincipal CurrentUser currentUser,
                                              @RequestParam("delivery_id") int id,
                                              @RequestParam("page") Optional<Integer> page,
                                              @RequestParam("size") Optional<Integer> size,
                                              ModelMap modelMap) {
        deliveryService.chooseDelivery(currentUser.getUser(), id, Status.DELIVERED);
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(9);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        Page<DeliveryDto> result = deliveryService.findAllByUserIdAndOrderStatus(currentUser.getUser().getId(), Status.IN_PROCESS, pageable);
        int totalPages = result.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .toList();
            modelMap.addAttribute("pageNumbers", pageNumbers);
        }
        modelMap.addAttribute("totalPages", totalPages);
        modelMap.addAttribute("currentPage", currentPage);
        modelMap.addAttribute("deliveries2", result);
        modelMap.addAttribute("user", userService.findByIdWithAddresses(currentUser.getUser().getId()));
        modelMap.addAttribute("deliveries1",
                deliveryService.findAllByOrderStatus(Status.APPROVED, pageable).getContent());
        return "account-delivery-custom";
    }
}
