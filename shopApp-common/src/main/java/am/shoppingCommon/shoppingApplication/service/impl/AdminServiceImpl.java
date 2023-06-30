package am.shoppingCommon.shoppingApplication.service.impl;

import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.entity.*;
import am.shoppingCommon.shoppingApplication.mapper.OrderMapper;
import am.shoppingCommon.shoppingApplication.repository.DeliveryRepository;
import am.shoppingCommon.shoppingApplication.repository.OrderRepository;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import am.shoppingCommon.shoppingApplication.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;

    @Override
    public void block(int id, User currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            Optional<User> byId = userRepository.findById(id);
            if (byId.isPresent()) {
                User user = byId.get();
                user.setEnabled(false);
                userRepository.save(user);
            }
        }
    }

    @Override
    public void unBlock(int id, User currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            Optional<User> byId = userRepository.findById(id);
            if (byId.isPresent()) {
                User user = byId.get();
                user.setEnabled(true);
                userRepository.save(user);
            }
        }
    }

    @Override
    @Transactional
    public Order editOrder(OrderDto orderDto, int deliveryId, User currentUser) {
//        if (currentUser.getRole() == Role.ADMIN) {

        Optional<Order> byId = orderRepository.findById(orderDto.getId());
        Optional<Delivery> deliveryOptional = deliveryRepository.findAllByOrder_Id(orderDto.getId());
        Optional<User> userOptional = userRepository.findById(deliveryId);

        if (byId.isPresent() && deliveryOptional.isPresent() && userOptional.isPresent()) {
            Order orderFromDb = byId.get();
            orderFromDb.setTotalAmount(orderDto.getTotalAmount());
            orderFromDb.setStatus(orderDto.getStatus());
            Delivery delivery = deliveryOptional.get();
            delivery.setUser(userOptional.get());
            deliveryRepository.save(delivery);

            return orderRepository.save(orderFromDb);
        }

        return null;
    }
}
