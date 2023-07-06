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

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @GetMapping
    public String orderPage(ModelMap modelMap,
                            @AuthenticationPrincipal CurrentUser currentUser) {
        UserDto byId = userService.findById(currentUser.getUser().getId());
        if (byId.getAddresses().size() == 0){
            return "redirect:/user/address";
        }
        modelMap.addAttribute("order",orderService
                .findByUserIdAndStatus(currentUser.getUser().getId(), Status.PENDING));
        modelMap.addAttribute("user", byId);
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
