package am.shoppingCommon.shoppingApplication.repository;


import am.shoppingCommon.shoppingApplication.entity.Delivery;
import am.shoppingCommon.shoppingApplication.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {

    Page<Delivery> findAllByUserIdAndOrderStatus(Integer id, Status status, Pageable pageable);

    Page<Delivery> findAllByOrderStatus(Status status, Pageable pageable);

    Optional<Delivery> findAllByOrder_Id(int orderId);

}
