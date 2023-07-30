package am.shoppingCommon.shoppingApplication.service.impl;


import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderResponseDto;
import am.shoppingCommon.shoppingApplication.entity.*;
import am.shoppingCommon.shoppingApplication.mapper.OrderMapper;
import am.shoppingCommon.shoppingApplication.repository.*;
import am.shoppingCommon.shoppingApplication.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Ashot Simonyan on 21.05.23.
 */

/**
 * Service implementation class responsible for handling order-related operations.
 * It implements the OrderService interface and provides functionalities for managing orders, order items, and order history.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;


    /**
     * Retrieves a list of all orders from the database.
     *
     * @return List of OrderResponseDto representing all orders in the database.
     */
    @Override
    public List<OrderResponseDto> findAllOrder() {
        List<Order> all = orderRepository.findAll();
        log.info("Get all orders");
        return OrderMapper.findAll(all);
    }


    /**
     * Retrieves a list of the last 15 orders for a user based on the provided user ID.
     *
     * @param id The ID of the user to retrieve orders for.
     * @return List of OrderDto representing the user's last 15 orders with pagination.
     */
    @Override
    public List<OrderDto> findAllByUserId(int id) {
        Pageable pageable = PageRequest.of(0, 15);
        List<Order> last15Orders = orderRepository.findLast15OrdersByUserId(id, pageable);
        log.info("Get all orders by {} user_id",id);
        return OrderMapper.listOrderToListOrderDto(last15Orders);
    }


    /**
     * Retrieves an order by its ID from the database.
     *
     * @param id The ID of the order to retrieve.
     * @return OrderDto representing the retrieved order if it exists; otherwise, it returns null.
     */
    @Override
    public OrderDto orderById(int id) {
        Optional<Order> byId = orderRepository.findById(id);
        log.info("Get {} id order",id);
        return byId.map(OrderMapper::orderToOrderDto).orElse(null);
    }


    /**
     * Retrieves the last 10 orders from the database.
     *
     * @return List of OrderDto representing the last 10 orders based on order date in descending order.
     */
    @Override
    public List<OrderDto> ordersLimit10() {
        List<Order> top10ByOrderByOrderDateDesc = orderRepository.findTop10ByOrderByDateTimeDesc();
        List<OrderDto> orderDtoList = OrderMapper.listOrderToListOrderDto(top10ByOrderByOrderDateDesc);
        log.info("Get last 10 orders");
        return orderDtoList;
    }


    /**
     * Retrieves the order for a user with the provided status from the database.
     *
     * @param id     The ID of the user to retrieve the order for.
     * @param status The status of the order to retrieve.
     * @return OrderDto representing the retrieved order if it exists; otherwise, it returns null.
     */
    @Override
    @Transactional
    public OrderDto findByUserIdAndStatus(int id, Status status) {
        Optional<Order> byUserIdAndStatus = orderRepository.findByUserIdAndStatus(id, status);
        log.info("Get order by {} user_id and {} order status",id,status);
        return byUserIdAndStatus.map(OrderMapper::orderToOrderDto).orElse(null);
    }

    /**
     * Removes a product from the order based on the provided product ID, order item ID, and user ID.
     * This method is transactional to ensure data consistency during the removal process.
     *
     * @param productId   The ID of the product to be removed from the order.
     * @param orderItemId The ID of the order item representing the product in the order.
     * @param userId      The ID of the user who placed the order.
     */
    @Override
    @Transactional
    public void removeByProductIdAndOrderItemId(int productId, int orderItemId, int userId) {
        Optional<OrderItem> orderItemOptional = orderItemRepository.findById(orderItemId);
        Optional<Product> productOptional = productRepository.findById(productId);

        if (orderItemOptional.isPresent() && productOptional.isPresent()) {
            OrderItem orderItem = orderItemOptional.get();
            Product product = productOptional.get();

            int count = orderItem.getCount();
            product.setCount(product.getCount() + count);

            Optional<Order> orderOptional = orderRepository.findByUserIdAndStatus(userId, Status.PENDING);
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();

                double itemPrice = orderItem.getProduct().getPrice();
                double removedAmount = itemPrice * count;

                double totalAmount = order.getTotalAmount() - removedAmount;
                order.setTotalAmount(totalAmount);

                orderItemRepository.deleteByProduct_IdAndId(productId, orderItemId);
            }
            log.info("orderItem is deleted from order by ID: {}", orderItem.getOrder().getId());
        }
    }


    /**
     * Saves an order for the user based on the provided user ID.
     * This method is transactional to ensure data consistency during the order creation process.
     *
     * @param userId The ID of the user for whom the order is being created.
     * @return OrderDto representing the saved order as a response.
     */
    @Override
    @Transactional
    public OrderDto save(int userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Order> byUserIdAndStatus = orderRepository.findByUserIdAndStatus(userId, Status.PENDING);

            if (byUserIdAndStatus.isEmpty()) {
                Order order = createNewOrder(user, userId);
                Order save = orderRepository.save(order);
                cartRepository.deleteByUserId(userId);
                log.info("new order is created by ID: {}", save.getId());
                return OrderMapper.orderToOrderDto(save);
            } else {
                Order existingOrder = byUserIdAndStatus.get();
                updateExistingOrder(existingOrder, userId);
                Order save = orderRepository.save(existingOrder);
                cartRepository.deleteByUserId(userId);
                log.info("order is updated by ID: {}", save.getId());
                return OrderMapper.orderToOrderDto(save);
            }
        }
        log.info("Order did not update");
        return null;
    }

    /**
     * Creates a new order for the user based on the cart contents.
     *
     * @param user   The user for whom the order is being created.
     * @param userId The ID of the user for whom the order is being created.
     * @return Order instance representing the newly created order.
     */
    private Order createNewOrder(User user, int userId) {
        Optional<Cart> allByUserId = cartRepository.findAllByUser_Id(userId);
        List<OrderItem> orderItems = new ArrayList<>();
        int totalAmount = 0;

        if (allByUserId.isPresent()) {
            Cart cart = allByUserId.get();
            List<CartItem> cartItems = cart.getCartItems();

            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setCount(cartItem.getCount());
                orderItem.setProduct(cartItem.getProduct());
                orderItems.add(orderItem);
                totalAmount += cartItem.getProduct().getPrice() * cartItem.getCount();
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);
        order.setStatus(Status.PENDING);
        log.info("Order was created");
        return order;
    }

    /**
     * Updates an existing order with new cart items for the user.
     *
     * @param existingOrder The existing order to be updated.
     * @param userId        The ID of the user for whom the order is being updated.
     */
    private void updateExistingOrder(Order existingOrder, int userId) {
        // Retrieve the user's cart from the cartRepository using the user ID.
        Optional<Cart> allByUserId = cartRepository.findAllByUser_Id(userId);

        // Check if the cart exists for the user.
        if (allByUserId.isPresent()) {
            Cart cart = allByUserId.get();
            List<CartItem> cartItems = cart.getCartItems();

            // Iterate through the cart items and update the existing order accordingly.
            for (CartItem cartItem : cartItems) {
                boolean found = false;

                for (OrderItem orderItem : existingOrder.getOrderItems()) {
                    if (orderItem.getProduct().getId() == cartItem.getProduct().getId()) {
                        orderItem.setCount(orderItem.getCount() + cartItem.getCount());
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setCount(cartItem.getCount());
                    orderItem.setProduct(cartItem.getProduct());
                    existingOrder.getOrderItems().add(orderItem);
                }

                existingOrder.setTotalAmount(existingOrder.getTotalAmount() + (cartItem.getProduct().getPrice() * cartItem.getCount()));
            }
        }
    }

}

