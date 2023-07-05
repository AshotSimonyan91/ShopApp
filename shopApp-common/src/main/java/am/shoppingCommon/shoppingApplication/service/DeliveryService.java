package am.shoppingCommon.shoppingApplication.service;


import am.shoppingCommon.shoppingApplication.dto.deliveryDto.DeliveryDto;
import am.shoppingCommon.shoppingApplication.entity.Delivery;
import am.shoppingCommon.shoppingApplication.entity.Status;
import am.shoppingCommon.shoppingApplication.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeliveryService {

    Page<DeliveryDto> findAllByUserIdAndOrderStatus(int id, Status status, Pageable pageable);
    Page<DeliveryDto> findAllByOrderStatus(Status status, Pageable pageable);

    DeliveryDto findById(int id);

    void remove(int id);

    DeliveryDto save(int id);
    DeliveryDto save(Delivery delivery);

    DeliveryDto chooseDelivery(User user, int id, Status status);

    DeliveryDto findByOrderId(int id);
}
