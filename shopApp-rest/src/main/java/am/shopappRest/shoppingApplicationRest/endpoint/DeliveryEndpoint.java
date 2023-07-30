package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shopappRest.shoppingApplicationRest.restDto.deliveryRequestDto.DeliveryRequestDto;
import am.shopappRest.shoppingApplicationRest.restDto.orderRequestDto.OrderPageDto;
import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.deliveryDto.DeliveryDto;
import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.entity.Status;
import am.shoppingCommon.shoppingApplication.mapper.UserMapper;
import am.shoppingCommon.shoppingApplication.service.DeliveryService;
import am.shoppingCommon.shoppingApplication.service.OrderService;
import am.shoppingCommon.shoppingApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

/**
 * This endpoint class handles various operations related to deliveries and orders. It provides endpoints for adding, removing,
 * and retrieving delivery information, as well as user and order details. Additionally, it offers pagination for deliveries
 * and customized delivery pages for specific orders. The class uses the DeliveryService, UserService, and OrderService to
 * perform the necessary business logic.
 */
@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class DeliveryEndpoint {
    private final DeliveryService deliveryService;
    private final UserService userService;
    private final OrderService orderService;

    /**
     * GET endpoint to add a new delivery for the specified order. It retrieves the order by its ID and creates a new delivery
     * for that order. The method then returns a ResponseEntity with the created DeliveryDto containing the delivery information.
     *
     * @param id The ID of the order for which the delivery needs to be added.
     * @return ResponseEntity containing the created DeliveryDto if successful, or a "bad request" status if the order is not found.
     */
    @GetMapping("/add")
    public ResponseEntity<DeliveryDto> addDelivery(@RequestParam("order_id") int id) {
        return ResponseEntity.ok(deliveryService.save(id));
    }

    /**
     * DELETE endpoint to remove a delivery by its ID. It deletes the delivery from the system and returns a ResponseEntity
     * with a "no content" status if successful.
     *
     * @param id The ID of the delivery to be removed.
     * @return ResponseEntity with "no content" status if successful.
     */
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeDelivery(@RequestParam("id") int id) {
        deliveryService.remove(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET endpoint to retrieve the user information for the specified delivery ID. It fetches the user details along with their addresses
     * by their ID and returns a ResponseEntity with the UserDto containing the user information if found. If the user is not found,
     * the method returns a "bad request" status.
     *
     * @param modelmap The ModelMap object to be populated with data for the view.
     * @param id       The ID of the user for which the delivery information is requested.
     * @return ResponseEntity containing the UserDto with user information if found, or a "bad request" status if the user is not found.
     */
    @GetMapping("/customer")
    public ResponseEntity<UserDto> deliveryUserPage(ModelMap modelmap,
                                                    @RequestParam("id") int id) {
        UserDto byIdWithAddresses = userService.findByIdWithAddresses(id);
        if (byIdWithAddresses != null) {
            return ResponseEntity.ok(byIdWithAddresses);
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * GET endpoint to retrieve the delivery and user information for the specified order ID. It fetches the user details along with their addresses
     * for the currently authenticated user and retrieves the order details by its ID. It then constructs an OrderPageDto containing the order
     * and user information and returns it as a ResponseEntity. If the order is not found, the method returns a "bad request" status.
     *
     * @param modelmap    The ModelMap object to be populated with data for the view.
     * @param currentUser The CurrentUser object representing the currently authenticated user.
     * @param id          The ID of the order for which the delivery information is requested.
     * @return ResponseEntity containing the OrderPageDto with order and user information if found, or a "bad request" status if the order is not found.
     */
    @GetMapping("/order")
    public ResponseEntity<OrderPageDto> deliveryOrderPage(ModelMap modelmap,
                                                          @AuthenticationPrincipal CurrentUser currentUser,
                                                          @RequestParam("id") int id) {
        UserDto byIdWithAddresses = userService.findByIdWithAddresses(currentUser.getUser().getId());
        OrderDto order = orderService.orderById(id);
        if (order != null) {
            OrderPageDto orderPageDto = new OrderPageDto();
            orderPageDto.setOrderDto(order);
            orderPageDto.setUserDto(byIdWithAddresses);
            return ResponseEntity.ok(orderPageDto);
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * GET endpoint to retrieve customized delivery pages for a specific delivery ID and status. It first chooses a delivery
     * for the specified ID and updates its status to "IN_PROCESS". The method then paginates the deliveries with the status "IN_PROCESS"
     * for the specified user and constructs a DeliveryRequestDto containing the pagination details and delivery information. It
     * returns this DeliveryRequestDto as a ResponseEntity.
     *
     * @param currentUser The CurrentUser object representing the currently authenticated user.
     * @param id          The ID of the delivery to be chosen and processed.
     * @param page        The page number for pagination (default is 1).
     * @param size        The page size for pagination (default is 9).
     * @return ResponseEntity containing the DeliveryRequestDto with pagination details and delivery information.
     */
    @GetMapping("/custom")
    public ResponseEntity<DeliveryRequestDto> getCustomDeliveryPage(@AuthenticationPrincipal CurrentUser currentUser,
                                                                    @RequestParam("delivery_id") int id,
                                                                    @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                    @RequestParam(value = "size", defaultValue = "9") Integer size) {
        deliveryService.chooseDelivery(currentUser.getUser(), id, Status.IN_PROCESS);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<DeliveryDto> result = deliveryService.findAllByUserIdAndOrderStatus(currentUser.getUser().getId(), Status.IN_PROCESS, pageable);
        int totalPages = result.getTotalPages();
        DeliveryRequestDto deliveryRequestDto = buildAccountDeliveryDto(currentUser, result, totalPages, page);
        return ResponseEntity.ok(deliveryRequestDto);
    }

    /**
     * GET endpoint to retrieve delivery pages for deliveries with status "IN_PROCESS". It paginates the deliveries with the status "IN_PROCESS"
     * for the specified user and constructs a DeliveryRequestDto containing the pagination details and delivery information. It
     * returns this DeliveryRequestDto as a ResponseEntity.
     *
     * @param currentUser The CurrentUser object representing the currently authenticated user.
     * @param page        The page number for pagination (default is 1).
     * @param size        The page size for pagination (default is 9).
     * @return ResponseEntity containing the DeliveryRequestDto with pagination details and delivery information.
     */
    @GetMapping("/inProcess")
    public ResponseEntity<DeliveryRequestDto> getInProcessDeliveryPage(@AuthenticationPrincipal CurrentUser currentUser,
                                                                       @RequestParam(value = "page",defaultValue = "1") Integer page,
                                                                       @RequestParam(value = "size",defaultValue = "9") Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<DeliveryDto> result = deliveryService.findAllByUserIdAndOrderStatus(currentUser.getUser().getId(), Status.IN_PROCESS, pageable);
        int totalPages = result.getTotalPages();
        DeliveryRequestDto deliveryRequestDto = buildAccountDeliveryDto(currentUser, result, totalPages, page);
        return ResponseEntity.ok(deliveryRequestDto);
    }

    /**
     * GET endpoint to retrieve customized delivery pages for a specific delivery ID and status "IN_PROCESS". It first chooses a delivery
     * for the specified ID and updates its status to "DELIVERED". The method then paginates the deliveries with the status "IN_PROCESS"
     * for the specified user and constructs a DeliveryRequestDto containing the pagination details and delivery information. It
     * returns this DeliveryRequestDto as a ResponseEntity.
     *
     * @param currentUser The CurrentUser object representing the currently authenticated user.
     * @param id          The ID of the delivery to be chosen and processed.
     * @param page        The page number for pagination (default is 1).
     * @param size        The page size for pagination (default is 9).
     * @return ResponseEntity containing the DeliveryRequestDto with pagination details and delivery information.
     */
    @GetMapping("/inProcess/custom")
    public ResponseEntity<DeliveryRequestDto> getCustomInProcessDeliveryPage(@AuthenticationPrincipal CurrentUser currentUser,
                                                                             @RequestParam("delivery_id") int id,
                                                                             @RequestParam(value = "page" ,defaultValue = "1") Integer page,
                                                                             @RequestParam(value = "size",defaultValue = "9") Integer size) {
        deliveryService.chooseDelivery(currentUser.getUser(), id, Status.DELIVERED);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<DeliveryDto> result = deliveryService.findAllByUserIdAndOrderStatus(currentUser.getUser().getId(), Status.IN_PROCESS, pageable);
        int totalPages = result.getTotalPages();
        DeliveryRequestDto deliveryRequestDto = buildAccountDeliveryDto(currentUser, result, totalPages, page);
        return ResponseEntity.ok(deliveryRequestDto);
    }

    /**
     * Private helper method to build a DeliveryRequestDto containing delivery and user information for pagination.
     * The method constructs the DeliveryRequestDto based on the provided pagination details, current user information,
     * and the deliveries with status "IN_PROCESS" and "APPROVED". It returns the constructed DeliveryRequestDto.
     *
     * @param currentUser The CurrentUser object representing the currently authenticated user.
     * @param result      The Page object containing the deliveries with status "IN_PROCESS" for pagination.
     * @param totalPages  The total number of pages for pagination.
     * @param currentPage The current page number for pagination.
     * @return The constructed DeliveryRequestDto containing pagination details and delivery information.
     */
    private DeliveryRequestDto buildAccountDeliveryDto(CurrentUser currentUser, Page<DeliveryDto> result, int totalPages, int currentPage) {
        DeliveryRequestDto deliveryRequestDto = new DeliveryRequestDto();
        deliveryRequestDto.setTotalPages(totalPages);
        deliveryRequestDto.setCurrentPage(currentPage);
        deliveryRequestDto.setDeliveries2(result.getContent());
        deliveryRequestDto.setUser(UserMapper.userToUserDto(currentUser.getUser()));
        deliveryRequestDto.setDeliveries1(deliveryService.findAllByOrderStatus(Status.APPROVED, result.getPageable()).getContent());
        return deliveryRequestDto;
    }
}
