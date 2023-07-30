package am.shopappweb.shopappweb.controller;


import am.shopappweb.shopappweb.security.CurrentUser;
import am.shoppingCommon.shoppingApplication.dto.cartDto.CartItemDto;
import am.shoppingCommon.shoppingApplication.dto.categoryDto.CategoryDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserShortDto;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.mapper.UserMapper;
import am.shoppingCommon.shoppingApplication.service.CartService;
import am.shoppingCommon.shoppingApplication.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * This controller advice class provides common attributes (model attributes) that can be used across multiple controllers/views.
 * It is responsible for handling user-related attributes, category data, and cart items for the current user.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class CurrentUserControllerAdvice {
    private final CartService cartService;

    private final CategoryService categoryService;

    /**
     * Provides the model attribute "currentUser" containing the UserShortDto representation of the currently authenticated user.
     * If a user is authenticated, their UserShortDto is returned; otherwise, null is returned.
     *
     * @param currentUser The currently authenticated user (CurrentUser) or null if no user is authenticated.
     * @return The UserShortDto representation of the authenticated user or null.
     */

    @ModelAttribute("currentUser")
    public UserShortDto currentUser(@AuthenticationPrincipal CurrentUser currentUser) {
        if (currentUser != null) {
            User user = currentUser.getUser();
            return UserMapper.userToUserShortDto(user);
        }
        return null;
    }

    /**
     * Provides the model attribute "women" containing a list of CategoryDto representing women-related categories.
     * The list of women categories is fetched from the CategoryService based on the "women" parent category.
     *
     * @return The list of CategoryDto representing women-related categories.
     */
    @ModelAttribute("women")
    public List<CategoryDto> getWomenCategories() {
        return categoryService.findByParent("women");
    }

    /**
     * Provides the model attribute "livingRoom" containing a list of CategoryDto representing living room-related categories.
     * The list of living room categories is fetched from the CategoryService based on the "livingRoom" parent category.
     *
     * @return The list of CategoryDto representing living room-related categories.
     */
    @ModelAttribute("livingRoom")
    public List<CategoryDto> getLivingRoomCategories() {
        return categoryService.findByParent("livingRoom");
    }

    /**
     * Provides the model attribute "cartItems" containing a list of CartItemDto representing cart items for the current user.
     * If a user is authenticated, their cart items are fetched from the CartService based on their ID.
     * If no user is authenticated, the cartItems attribute is set to null.
     *
     * @param currentUser The currently authenticated user (CurrentUser) or null if no user is authenticated.
     * @return The list of CartItemDto representing cart items for the authenticated user or null if no user is authenticated.
     */
    @ModelAttribute("cartItems")
    public List<CartItemDto> currentUserCart(@AuthenticationPrincipal CurrentUser currentUser) {
        if (currentUser != null) {
            return cartService.findLastCartItemsByLimit(currentUser.getUser().getId());
        }
        return null;
    }
}