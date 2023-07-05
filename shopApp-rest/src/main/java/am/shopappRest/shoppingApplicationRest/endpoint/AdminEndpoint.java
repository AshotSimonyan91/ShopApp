package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shopappRest.shoppingApplicationRest.restDto.adminRequestDto.AdminPageDto;
import am.shopappRest.shoppingApplicationRest.restDto.adminRequestDto.AllUsersPageDto;
import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.mapper.CategoryMapper;
import am.shoppingCommon.shoppingApplication.mapper.UserMapper;
import am.shoppingCommon.shoppingApplication.service.CategoryService;
import am.shoppingCommon.shoppingApplication.service.OrderService;
import am.shoppingCommon.shoppingApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminEndpoint {
    private final UserService userService;
    private final OrderService orderService;
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<AdminPageDto> adminPage(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(AdminPageDto.builder()
                .userDto(userService.findById(currentUser.getUser().getId()))
                .orderDtoList(orderService.ordersLimit10())
                .build());
    }

    @DeleteMapping("remove")
    public ResponseEntity<?> removeUser(@RequestParam("id") int id) {
        userService.removeById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("update")
    public ResponseEntity<UserDto> updateUserPage(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(userService.findById(currentUser.getUser().getId()));
    }

    @GetMapping("/all")
    public ResponseEntity<AllUsersPageDto> allUsersPage(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(AllUsersPageDto.builder()
                .currentUser(userService.findById(currentUser.getUser().getId()))
                .userDtoList(userService.findAll(Pageable.ofSize(9)).getContent())
                .build());
    }

    @GetMapping("/add/product")
    public ResponseEntity<List<CategoryDto>> addProductAdminPage() {
        return ResponseEntity.ok(categoryService.findAllCategory());
    }
}
