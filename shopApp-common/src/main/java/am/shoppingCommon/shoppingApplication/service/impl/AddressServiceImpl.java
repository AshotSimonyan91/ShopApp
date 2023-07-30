package am.shoppingCommon.shoppingApplication.service.impl;



import am.shoppingCommon.shoppingApplication.service.AddressService;
import am.shoppingCommon.shoppingApplication.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * This class is an implementation of the AddressService interface, providing address-related functionalities
 * for a shopping application. It is a service component managed by Spring and utilizes the AddressRepository
 * to interact with the underlying data storage.
 *
 * The class allows deleting an address by its ID using the delete() method.
 */
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;

    /**
     * Implementation of the AddressService interface to delete an address by its ID.
     *
     * @param id The unique identifier of the address to be deleted.
     * @throws DataAccessException If there's an issue accessing the data storage (e.g., database error).
     */
    @Override
    public void delete(int id) {
        addressRepository.deleteById(id);
    }
}