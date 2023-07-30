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
 * This class provides the implementation for the DeliveryService interface, offering functionalities
 * related to managing deliveries and orders in a shopping application. It handles operations such as
 * retrieving deliveries by various criteria, saving new deliveries, updating delivery details,
 * and finding deliveries by order ID. The class uses repositories to interact with the underlying data storage
 * and a custom DeliveryMapper to convert entities to DTOs and vice versa.
 */
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;


    /**
     * Retrieves a paginated list of deliveries for a specific user and order status.
     *
     * @param id       The unique identifier of the user whose deliveries are to be retrieved.
     * @param status   The order status to filter deliveries.
     * @param pageable The pageable object for pagination and sorting.
     * @return The Page of DeliveryDto representing the paginated list of deliveries for the user and order status.
     */
    @Override
    public Page<DeliveryDto> findAllByUserIdAndOrderStatus(int id, Status status, Pageable pageable) {
        Page<Delivery> allByUserIdAndOrderStatus = deliveryRepository.findAllByUserIdAndOrderStatus(id, status, pageable);
        return DeliveryMapper.mapPageToDto(allByUserIdAndOrderStatus);
    }

    /**
     * Retrieves a paginated list of deliveries for a specific order status.
     *
     * @param status   The order status to filter deliveries.
     * @param pageable The pageable object for pagination and sorting.
     * @return The Page of DeliveryDto representing the paginated list of deliveries for the specified order status.
     */
    @Override
    public Page<DeliveryDto> findAllByOrderStatus(Status status, Pageable pageable) {
        Page<Delivery> allByOrderStatus = deliveryRepository.findAllByOrderStatus(status, pageable);
        return DeliveryMapper.mapPageToDto(allByOrderStatus);
    }

    /**
     * Retrieves the delivery with the specified ID from the database.
     *
     * @param id The unique identifier of the delivery to be retrieved.
     * @return The DeliveryDto representing the retrieved delivery, or null if no delivery is found.
     */
    @Override
    public DeliveryDto findById(int id) {
        Optional<Delivery> byId = deliveryRepository.findById(id);
        return byId.map(DeliveryMapper::mapToDto).orElse(null);
    }

    /**
     * Removes the delivery with the specified ID from the database.
     *
     * @param id The unique identifier of the delivery to be removed.
     */
    @Override
    public void remove(int id) {
        deliveryRepository.deleteById(id);
    }

    /**
     * Creates and saves a new delivery for the order with the provided ID.
     * The associated order status is set to APPROVED.
     *
     * @param id The unique identifier of the order to be associated with the new delivery.
     * @return The DeliveryDto representing the newly created and saved delivery.
     */
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

    /**
     * Saves the provided delivery to the database.
     *
     * @param delivery The Delivery entity to be saved.
     * @return The DeliveryDto representing the saved delivery.
     */
    @Override
    public DeliveryDto save(Delivery delivery) {
        Delivery save = deliveryRepository.save(delivery);
        return DeliveryMapper.mapToDto(save);
    }

    /**
     * Associates the provided user with the delivery having the specified ID, and updates the order status to the provided status.
     *
     * @param user   The user to be associated with the delivery.
     * @param id     The unique identifier of the delivery to be updated.
     * @param status The new status to be set for the associated order.
     * @return The DeliveryDto representing the updated delivery after user association and order status update.
     */
    @Override
    public DeliveryDto chooseDelivery(User user, int id, Status status) {
        Delivery delivery = deliveryRepository.findById(id).orElse(null);
        Optional<User> byId = userRepository.findById(user.getId());
        delivery.setUser(byId.orElse(null));
        delivery.getOrder().setStatus(status);
        Delivery save = deliveryRepository.save(delivery);
        return DeliveryMapper.mapToDto(save);
    }

    /**
     * Retrieves the delivery associated with the order having the specified ID from the database.
     *
     * @param id The unique identifier of the order whose delivery is to be retrieved.
     * @return The DeliveryDto representing the retrieved delivery, or null if no delivery is found.
     */
    @Override
    public DeliveryDto findByOrderId(int id) {
        Optional<Delivery> allByOrderId = deliveryRepository.findAllByOrder_Id(id);
        return allByOrderId.map(DeliveryMapper::mapToDto).orElse(null);
    }
}
