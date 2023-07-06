package am.shoppingCommon.shoppingApplication.service.impl;


import am.shoppingCommon.shoppingApplication.dto.deliveryDto.DeliveryDto;
import am.shoppingCommon.shoppingApplication.mapper.DeliveryMapper;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import am.shoppingCommon.shoppingApplication.service.DeliveryService;
import am.shoppingCommon.shoppingApplication.entity.Delivery;
import am.shoppingCommon.shoppingApplication.entity.Order;
import am.shoppingCommon.shoppingApplication.entity.Status;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.repository.DeliveryRepository;
import am.shoppingCommon.shoppingApplication.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by Ashot Simonyan on 21.05.23.
 */
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;


    @Override
    public Page<DeliveryDto> findAllByUserIdAndOrderStatus(int id, Status status, Pageable pageable) {
        Page<Delivery> allByUserIdAndOrderStatus = deliveryRepository.findAllByUserIdAndOrderStatus(id, status, pageable);
        return DeliveryMapper.mapPageToDto(allByUserIdAndOrderStatus);
    }

    @Override
    public Page<DeliveryDto> findAllByOrderStatus(Status status, Pageable pageable) {
        Page<Delivery> allByOrderStatus = deliveryRepository.findAllByOrderStatus(status, pageable);
        return DeliveryMapper.mapPageToDto(allByOrderStatus);
    }

    @Override
    public DeliveryDto findById(int id) {
        Optional<Delivery> byId = deliveryRepository.findById(id);
        return byId.map(DeliveryMapper::mapToDto).orElse(null);
    }

    @Override
    public void remove(int id) {
        deliveryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public DeliveryDto save(int id) {
        Optional<Order> byId = orderRepository.findById(id);
        if (byId.isPresent()) {
            Order order = byId.get();
            Delivery delivery = new Delivery();
            delivery.setOrder(order);
            Delivery save = deliveryRepository.save(delivery);
            order.setStatus(Status.APPROVED);
            return DeliveryMapper.mapToDto(save);
        }
        return null;
    }

    @Override
    public DeliveryDto save(Delivery delivery) {
        Delivery save = deliveryRepository.save(delivery);
        return DeliveryMapper.mapToDto(save);
    }

    @Override
    public DeliveryDto chooseDelivery(User user, int id, Status status) {
        Delivery delivery = deliveryRepository.findById(id).orElse(null);
        Optional<User> byId = userRepository.findById(user.getId());
        delivery.setUser(byId.orElse(null));
        delivery.getOrder().setStatus(status);
        Delivery save = deliveryRepository.save(delivery);
        return DeliveryMapper.mapToDto(save);
    }

    @Override
    public DeliveryDto findByOrderId(int id) {
        Optional<Delivery> allByOrderId = deliveryRepository.findAllByOrder_Id(id);
        return allByOrderId.map(DeliveryMapper::mapToDto).orElse(null);
    }
}
