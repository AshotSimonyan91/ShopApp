package am.shoppingCommon.shoppingApplication.repository;

import am.shoppingCommon.shoppingApplication.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Integer> {
}
