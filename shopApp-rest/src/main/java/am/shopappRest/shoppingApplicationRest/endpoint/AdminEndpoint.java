package am.shopappRest.shoppingApplicationRest.endpoint;

import am.shopappRest.shoppingApplicationRest.restDto.adminRequestDto.AdminPageDto;
import am.shopappRest.shoppingApplicationRest.restDto.adminRequestDto.AllUsersPageDto;
import am.shopappRest.shoppingApplicationRest.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.service.CategoryService;
import am.shoppingCommon.shoppingApplication.service.OrderService;
import am.shoppingCommon.shoppingApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This endpoint class provides REST API endpoints for handling administrative tasks in the shopping application.
 * It allows authorized admin users to access and perform various actions related to users, orders, and categories.
 * The class is secured using Spring Security's @AuthenticationPrincipal to get the current authenticated user.
 * The endpoints return ResponseEntity objects with appropriate response data.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminEndpoint {
    private final UserService userService;
    private final OrderService orderService;
    private final CategoryService categoryService;

    /**
     * GET endpoint that retrieves the admin dashboard page data. It returns an AdminPageDto containing information
     * about the current authenticated admin user and the latest 10 orders.
     *
     * @param currentUser The CurrentUser object representing the currently authenticated user.
     * @return ResponseEntity containing the AdminPageDto with admin user and order data.
     */
    @GetMapping
    public ResponseEntity<AdminPageDto> adminPage(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(AdminPageDto.builder()
                .userDto(userService.findById(currentUser.getUser().getId()))
                .orderDtoList(orderService.ordersLimit10())
                .build());
    }

    /**
     * DELETE endpoint that handles removing a user by their ID. It deletes the user with the specified ID
     * from the database.
     *
     * @param id The ID of the user to be removed.
     * @return ResponseEntity with no content to indicate successful deletion.
     */
    @DeleteMapping("remove")
    public ResponseEntity<?> removeUser(@RequestParam("id") int id) {
        userService.removeById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET endpoint that retrieves the user update page data for the currently authenticated user (admin).
     * It returns the UserDto containing user information based on the current admin user's ID.
     *
     * @param currentUser The CurrentUser object representing the currently authenticated admin user.
     * @return ResponseEntity containing the UserDto with user information for update.
     */

    @GetMapping("update")
    public ResponseEntity<UserDto> updateUserPage(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(userService.findById(currentUser.getUser().getId()));
    }

    /**
     * GET endpoint that retrieves the page with information about all registered users. It returns an AllUsersPageDto
     * containing the currently authenticated admin user's information and a list of userDto objects representing
     * the users on the page.
     *
     * @param currentUser The CurrentUser object representing the currently authenticated admin user.
     * @return ResponseEntity containing the AllUsersPageDto with admin user and user list data.
     */
    @GetMapping("/all")
    public ResponseEntity<AllUsersPageDto> allUsersPage(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(AllUsersPageDto.builder()
                .currentUser(userService.findById(currentUser.getUser().getId()))
                .userDtoList(userService.findAll(Pageable.ofSize(9)).getContent())
                .build());
    }

    /**
     * GET endpoint that retrieves the page data for adding a new product by an admin user. It returns a list of
     * CategoryDto objects representing all available categories for product selection.
     *
     * @return ResponseEntity containing the list of CategoryDto objects representing available categories.
     */
    @GetMapping("/add/product")
    public ResponseEntity<List<CategoryDto>> addProductAdminPage() {
        return ResponseEntity.ok(categoryService.findAllCategory());
    }
}
