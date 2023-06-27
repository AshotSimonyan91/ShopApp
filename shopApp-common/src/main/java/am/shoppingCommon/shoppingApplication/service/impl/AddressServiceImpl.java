package am.shoppingCommon.shoppingApplication.service.impl;



import am.shoppingCommon.shoppingApplication.service.AddressService;
import am.shoppingCommon.shoppingApplication.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;

    @Override
    public void delete(int id) {
        addressRepository.deleteById(id);
    }
}