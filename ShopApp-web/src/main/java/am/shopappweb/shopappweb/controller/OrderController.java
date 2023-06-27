package am.shopappweb.shopappweb.controller;


import am.shoppingCommon.shoppingApplication.entity.Order;
import am.shoppingCommon.shoppingApplication.entity.Status;
import am.shoppingCommon.shoppingApplication.mapper.OrderMapper;
import am.shopappweb.shopappweb.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public String orderPage(ModelMap modelMap,
                            @AuthenticationPrincipal CurrentUser currentUser) {
        Optional<Order> byUserIdAndStatus = orderService
                .findByUserIdAndStatus(currentUser.getUser().getId(), Status.PENDING);
        modelMap.addAttribute("order", OrderMapper.orderToOrderDto(byUserIdAndStatus.orElse(null)));
        return "checkout";
    }

    @PostMapping("/add")
    public String addOrder(@AuthenticationPrincipal CurrentUser currentUser) {
        orderService.save(currentUser.getUser().getId());
        return "redirect:/cart";
    }

    @GetMapping("/remove")
    public String removeProductFromOrder(@RequestParam("product_id") int product_id,
                                         @RequestParam("orderItem_id") int orderItem_id,
                                         @AuthenticationPrincipal CurrentUser currentUser) {
        orderService.removeByProductIdAndOrderItemId(product_id, orderItem_id, currentUser.getUser().getId());
        return "redirect:/order";
    }
}
