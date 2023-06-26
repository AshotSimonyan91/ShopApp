package am.shoppingCommon.shoppingApplication.repository;


import am.shoppingCommon.shoppingApplication.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    Optional<Cart> findAllByUser_Id(int userId);


    void deleteByUserId(int userId);
}
