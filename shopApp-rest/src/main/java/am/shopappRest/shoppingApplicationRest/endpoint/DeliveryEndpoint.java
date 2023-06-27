package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shopappRest.shoppingApplicationRest.restDto.deliveryRequestDto.DeliveryRequestDto;
import am.shopappRest.shoppingApplicationRest.restDto.orderRequestDto.OrderPageDto;
import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.entity.Delivery;
import am.shoppingCommon.shoppingApplication.entity.Order;
import am.shoppingCommon.shoppingApplication.entity.Status;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.mapper.OrderMapper;
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

import java.util.Optional;

@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class DeliveryEndpoint {
    private final DeliveryService deliveryService;
    private final UserService userService;
    private final OrderService orderService;

    @GetMapping("/add")
    public ResponseEntity<?> addDelivery(@RequestParam("order_id") int id) {
        deliveryService.save(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeDelivery(@RequestParam("id") int id, @RequestParam("ProductId") int productId) {
        deliveryService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer")
    public ResponseEntity<UserDto> deliveryUserPage(ModelMap modelmap,
                                                    @RequestParam("id") int id) {
        User byIdWithAddresses = userService.findByIdWithAddresses(id);
        if (byIdWithAddresses != null) {
            return ResponseEntity.ok(UserMapper.userToUserDto(byIdWithAddresses));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/order")
    public ResponseEntity<OrderPageDto> deliveryOrderPage(ModelMap modelmap,
                                                          @AuthenticationPrincipal CurrentUser currentUser,
                                                          @RequestParam("id") int id) {
        User byIdWithAddresses = userService.findByIdWithAddresses(currentUser.getUser().getId());
        Order order = orderService.findById(id).orElse(null);
        if (order != null) {
            OrderPageDto orderPageDto = new OrderPageDto();
            orderPageDto.setOrderDto(OrderMapper.orderToOrderDto(order));
            orderPageDto.setUserDto(UserMapper.userToUserDto(byIdWithAddresses));
            return ResponseEntity.ok(orderPageDto);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/custom")
    public ResponseEntity<DeliveryRequestDto> getCustomDeliveryPage(@AuthenticationPrincipal CurrentUser currentUser,
                                                                    @RequestParam("delivery_id") int id,
                                                                    @RequestParam("page") Optional<Integer> page,
                                                                    @RequestParam("size") Optional<Integer> size) {
        deliveryService.chooseDelivery(currentUser.getUser(), id, Status.IN_PROCESS);
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(9);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        Page<Delivery> result = deliveryService.findAllByUserIdAndOrderStatus(currentUser.getUser().getId(), Status.IN_PROCESS, pageable);
        int totalPages = result.getTotalPages();
        DeliveryRequestDto deliveryRequestDto = buildAccountDeliveryDto(currentUser, result, totalPages, currentPage);
        return ResponseEntity.ok(deliveryRequestDto);
    }

    @GetMapping("/inProcess")
    public ResponseEntity<DeliveryRequestDto> getInProcessDeliveryPage(@AuthenticationPrincipal CurrentUser currentUser,
                                                                       @RequestParam("page") Optional<Integer> page,
                                                                       @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(9);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        Page<Delivery> result = deliveryService.findAllByUserIdAndOrderStatus(currentUser.getUser().getId(), Status.IN_PROCESS, pageable);
        int totalPages = result.getTotalPages();
        DeliveryRequestDto deliveryRequestDto = buildAccountDeliveryDto(currentUser, result, totalPages, currentPage);
        return ResponseEntity.ok(deliveryRequestDto);
    }

    @GetMapping("/inProcess/custom")
    public ResponseEntity<DeliveryRequestDto> getCustomInProcessDeliveryPage(@AuthenticationPrincipal CurrentUser currentUser,
                                                                             @RequestParam("delivery_id") int id,
                                                                             @RequestParam("page") Optional<Integer> page,
                                                                             @RequestParam("size") Optional<Integer> size) {
        deliveryService.chooseDelivery(currentUser.getUser(), id, Status.DELIVERED);
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(9);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
        Page<Delivery> result = deliveryService.findAllByUserIdAndOrderStatus(currentUser.getUser().getId(), Status.IN_PROCESS, pageable);
        int totalPages = result.getTotalPages();
        DeliveryRequestDto deliveryRequestDto = buildAccountDeliveryDto(currentUser, result, totalPages, currentPage);
        return ResponseEntity.ok(deliveryRequestDto);
    }

    private DeliveryRequestDto buildAccountDeliveryDto(CurrentUser currentUser, Page<Delivery> result, int totalPages, int currentPage) {
        DeliveryRequestDto deliveryRequestDto = new DeliveryRequestDto();
        deliveryRequestDto.setTotalPages(totalPages);
        deliveryRequestDto.setCurrentPage(currentPage);
        deliveryRequestDto.setDeliveries2(result.getContent());
        deliveryRequestDto.setUser(UserMapper.userToUserDto(currentUser.getUser()));
        deliveryRequestDto.setDeliveries1(deliveryService.findAllByOrderStatus(Status.APPROVED, result.getPageable()).getContent());
        return deliveryRequestDto;
    }
}
