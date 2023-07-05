package am.shoppingCommon.shoppingApplication.service.impl;


import am.shoppingCommon.shoppingApplication.service.OrderService;
import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderResponseDto;
import am.shoppingCommon.shoppingApplication.entity.*;
import am.shoppingCommon.shoppingApplication.mapper.OrderMapper;
import am.shoppingCommon.shoppingApplication.repository.*;
import lombok.RequiredArgsConstructor;
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
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;


    @Override
    public List<OrderResponseDto> findAllOrder() {
        List<Order> all = orderRepository.findAll();
        return OrderMapper.findAll(all);
    }

    @Override
    public List<OrderDto> findAllByUserId(int id) {
        Pageable pageable = PageRequest.of(0, 15);
        List<Order> last15Orders = orderRepository.findLast15OrdersByUserId(id, pageable);
        return OrderMapper.listOrderToListOrderDto(last15Orders);
    }

    @Override
    public OrderDto findById(int id) {
        Optional<Order> byId = orderRepository.findById(id);
        return byId.map(OrderMapper::orderToOrderDto).orElse(null);
    }

    @Override
    public OrderDto orderById(int id) {
        Optional<Order> byId = orderRepository.findById(id);
        return byId.map(OrderMapper::orderToOrderDto).orElse(null);
    }

    @Override
    public List<OrderDto> ordersLimit10() {
        List<Order> top10ByOrderByOrderDateDesc = orderRepository.findTop10ByOrderByDateTimeDesc();
        List<OrderDto> orderDtoList = OrderMapper.listOrderToListOrderDto(top10ByOrderByOrderDateDesc);
        return orderDtoList;
    }

    @Override
    @Transactional
    public OrderDto findByUserIdAndStatus(int id, Status status) {
        Optional<Order> byUserIdAndStatus = orderRepository.findByUserIdAndStatus(id, status);
        return byUserIdAndStatus.map(OrderMapper::orderToOrderDto).orElse(null);
    }

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
        }
    }

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
                return OrderMapper.orderToOrderDto(save);
            } else {
                Order existingOrder = byUserIdAndStatus.get();
                updateExistingOrder(existingOrder, userId);
                Order save = orderRepository.save(existingOrder);
                cartRepository.deleteByUserId(userId);
                return OrderMapper.orderToOrderDto(save);
            }
        }
        return null;
    }

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

        return order;
    }

    private void updateExistingOrder(Order existingOrder, int userId) {
        Optional<Cart> allByUserId = cartRepository.findAllByUser_Id(userId);

        if (allByUserId.isPresent()) {
            Cart cart = allByUserId.get();
            List<CartItem> cartItems = cart.getCartItems();

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

