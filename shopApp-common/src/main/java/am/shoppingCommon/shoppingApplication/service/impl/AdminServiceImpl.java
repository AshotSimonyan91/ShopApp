package am.shoppingCommon.shoppingApplication.service.impl;

import am.shoppingCommon.shoppingApplication.dto.orderDto.OrderDto;
import am.shoppingCommon.shoppingApplication.entity.Order;
import am.shoppingCommon.shoppingApplication.entity.Role;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.mapper.OrderMapper;
import am.shoppingCommon.shoppingApplication.repository.OrderRepository;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import am.shoppingCommon.shoppingApplication.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

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
    public Order editOrder(OrderDto orderDto, User currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            Order order = OrderMapper.orderDtoToOrder(orderDto);
           return orderRepository.save(order);
        }
        return null;
    }
}
