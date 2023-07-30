package am.shoppingCommon.shoppingApplication.service.impl;


import am.shoppingCommon.shoppingApplication.dto.cartDto.CartDto;
import am.shoppingCommon.shoppingApplication.dto.cartDto.CartItemDto;
import am.shoppingCommon.shoppingApplication.entity.Cart;
import am.shoppingCommon.shoppingApplication.entity.CartItem;
import am.shoppingCommon.shoppingApplication.entity.Product;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.mapper.CartMapper;
import am.shoppingCommon.shoppingApplication.repository.CartItemRepository;
import am.shoppingCommon.shoppingApplication.repository.CartRepository;
import am.shoppingCommon.shoppingApplication.repository.ProductRepository;
import am.shoppingCommon.shoppingApplication.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class provides the implementation for the CartService interface, offering functionalities
 * related to managing the user's cart in a shopping application. It handles operations such as finding
 * last cart items, retrieving the cart by user ID, adding products to the cart, removing products from the cart,
 * and updating the counts of cart items. The class uses repositories to interact with the underlying data storage
 * and a custom CartMapper to convert entities to DTOs and vice versa.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    /**
     * Retrieves the last four cart items for the specified user ID, representing the recently added products
     * in the user's cart. If the user has a cart and there are cart items present, it returns the CartItemDto
     * list; otherwise, it returns null.
     *
     * @param userId The unique identifier of the user whose cart items are to be retrieved.
     * @return The List of CartItemDto containing the last four cart items for the user, or null if no cart is found.
     */
    @Override
    public List<CartItemDto> findLastCartItemsByLimit(int userId) {
        Optional<Cart> allByUserId = cartRepository.findAllByUser_Id(userId);
        if (allByUserId.isPresent()) {
            Cart cart = allByUserId.get();
            List<CartItem> top4ByCartUserIdOrderByCreatedAtDesc = cartItemRepository.findTop4ByCartIdOrderByIdDesc(cart.getId());
            return CartMapper.mapToDtoList(top4ByCartUserIdOrderByCreatedAtDesc);
        }
        return null;
    }

    /**
     * Retrieves the cart for the specified user ID. If the user has a cart, it returns the CartDto
     * representation of the user's cart; otherwise, it returns null.
     *
     * @param id The unique identifier of the user whose cart is to be retrieved.
     * @return The CartDto representing the user's cart, or null if no cart is found.
     */
    @Override
    public CartDto findAllByUser_id(int id) {
        Optional<Cart> allByUserId = cartRepository.findAllByUser_Id(id);
        if (allByUserId.isPresent()) {
            Cart cart = allByUserId.get();
            return CartMapper.convertToDto(cart);
        }
        return null;
    }

    /**
     * Adds a product to the user's cart or increments the count if the product already exists in the cart.
     * If the user doesn't have a cart, a new cart is created for the user. The cart item count is updated, and
     * the product count is reduced accordingly. The updated cart is then saved to the database.
     *
     * @param id   The unique identifier of the product to be added to the cart.
     * @param user The user to whom the cart belongs.
     * @return The CartDto representing the updated cart.
     */
    @Override
    @Transactional
    public CartDto save(int id, User user) {
        Optional<Cart> cartOptional = cartRepository.findAllByUser_Id(user.getId());
        Cart cart = cartOptional.orElseGet(() -> createNewCart(user));

        List<CartItem> cartItems = cart.getCartItems();
        Optional<Product> productOptional = productRepository.findById(id);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            CartItem existingCartItem = findExistingCartItem(cartItems, product);

            if (existingCartItem != null) {
                incrementCartItem(existingCartItem);
            } else {
                product.setCount(product.getCount() - 1);
                productRepository.save(product);

                CartItem cartItem = createCartItem(product, cart);
                cartItems.add(cartItem);
            }

            cartItemRepository.saveAll(cartItems);
            productRepository.save(product);
        }
        Cart save = cartRepository.save(cart);
        log.info("cart is saved by userID: {} & by cartId :{}", user.getId(), id);
        return CartMapper.convertToDto(save);
    }

    /**
     * Removes a product from the user's cart based on the provided cart ID and product ID. The count of the product
     * is increased by the specified count value, and the corresponding cart item is deleted from the database.
     *
     * @param cartId    The unique identifier of the cart to be modified.
     * @param productId The unique identifier of the product to be removed from the cart.
     * @param count     The count of the product to be added back to the product's stock.
     */
    @Override
    @Transactional
    public void remove(int cartId, int productId, int count) {
        Optional<Cart> byId = cartRepository.findAllByUser_Id(cartId);
        Optional<Product> productById = productRepository.findById(productId);
        if (productById.isPresent()) {
            Product product = productById.get();
            product.setCount(product.getCount() + count);
        }
        cartItemRepository.deleteByCart_IdAndProduct_Id(byId.get().getId(), productId);
        log.info("cartItem is deleted by ID: {}", cartId);

    }


    /**
     * Updates the counts of multiple cart items based on their cart item IDs. The count of each cart item is adjusted
     * according to the provided counts list. If a count is null or less than or equal to zero, the method returns false,
     * indicating an invalid update request. It also checks if the product count is sufficient to accommodate the changes.
     *
     * @param cartItemIds The list of cart item IDs whose counts are to be updated.
     * @param counts      The list of updated counts for the corresponding cart items.
     * @return True if all updates are valid and successful; otherwise, false.
     */
    @Override
    @Transactional
    public boolean updateCartItemCounts(List<Integer> cartItemIds, List<Integer> counts) {
        boolean isValid = false;
        for (int i = 0; i < cartItemIds.size(); i++) {
            if (counts.get(i) == null || counts.get(i) <= 0) {
                return isValid;
            }
            Optional<CartItem> byId = cartItemRepository.findById(cartItemIds.get(i));
            CartItem cartItem = byId.get();
            Optional<Product> productOptional = productRepository.findById(cartItem.getProduct().getId());
            Product product = productOptional.get();
            if (product.getCount() - counts.get(i) + cartItem.getCount() < 0) {
                return isValid;
            }
            product.setCount((product.getCount() - counts.get(i)) + cartItem.getCount());
            cartItem.setCount(counts.get(i));
            isValid = true;
        }
        log.info("cartItem count is updated by IDs: {}", cartItemIds);
        return isValid;
    }

    /**
     * Creates a new Cart entity and associates it with the provided user.
     *
     * @param user The user to whom the new cart is associated.
     * @return The newly created Cart entity.
     */
    private Cart createNewCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setCartItems(new ArrayList<>());
        return cart;
    }

    /**
     * Finds an existing cart item in the given list of cart items based on the provided product.
     *
     * @param cartItems The list of cart items to search in.
     * @param product   The product whose cart item is to be found.
     * @return The CartItem entity representing the cart item if found; otherwise, null.
     */
    private CartItem findExistingCartItem(List<CartItem> cartItems, Product product) {
        return cartItems.stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst()
                .orElse(null);
    }

    /**
     * Increments the count of the specified cart item and decreases the product count by one.
     *
     * @param cartItem The CartItem entity whose count is to be incremented.
     */
    private void incrementCartItem(CartItem cartItem) {
        cartItem.setCount(cartItem.getCount() + 1);
        cartItem.getProduct().setCount(cartItem.getProduct().getCount() - 1);
    }

    /**
     * Creates a new CartItem entity associated with the provided product and cart.
     *
     * @param product The product to be associated with the cart item.
     * @param cart    The cart to be associated with the cart item.
     * @return The newly created CartItem entity.
     */
    private CartItem createCartItem(Product product, Cart cart) {
        CartItem cartItem = new CartItem();
        cartItem.setCount(1);
        cartItem.setProduct(product);
        cartItem.setCart(cart);
        return cartItem;
    }
}
