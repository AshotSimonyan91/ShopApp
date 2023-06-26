package am.shoppingCommon.shoppingApplication.repository;



import am.shoppingCommon.shoppingApplication.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    void deleteByProduct_IdAndId(int product_id,int orderItem_id);

    List<OrderItem> findAllByOrder_Id(int id);
}
