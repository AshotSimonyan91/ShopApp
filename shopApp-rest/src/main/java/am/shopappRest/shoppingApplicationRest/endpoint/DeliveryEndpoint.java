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

@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class DeliveryEndpoint {
    private final DeliveryService deliveryService;
    private final UserService userService;
    private final OrderService orderService;

    @GetMapping("/add")
    public ResponseEntity<DeliveryDto> addDelivery(@RequestParam("order_id") int id) {
        return ResponseEntity.ok(deliveryService.save(id));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeDelivery(@RequestParam("id") int id, @RequestParam("ProductId") int productId) {
        deliveryService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer")
    public ResponseEntity<UserDto> deliveryUserPage(ModelMap modelmap,
                                                    @RequestParam("id") int id) {
        UserDto byIdWithAddresses = userService.findByIdWithAddresses(id);
        if (byIdWithAddresses != null) {
            return ResponseEntity.ok(byIdWithAddresses);
        }
        return ResponseEntity.badRequest().build();
    }

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
