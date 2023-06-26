package am.shoppingCommon.shoppingApplication.repository;



import am.shoppingCommon.shoppingApplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

}
