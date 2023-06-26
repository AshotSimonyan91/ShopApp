package am.shoppingCommon.shoppingApplication.mapper;


import am.shoppingCommon.shoppingApplication.dto.addressDto.AddressDto;
import am.shoppingCommon.shoppingApplication.entity.Address;

/**
 * Created by Ashot Simonyan on 09.06.23.
 */

public class AddressMapper {

    public static AddressDto addressToAddressDto(Address address) {
        if (address == null) {
            return null;
        }
        AddressDto addressDto = new AddressDto();
        addressDto.setId(address.getId());
        addressDto.setCountry(address.getCountry());
        addressDto.setCity(address.getCity());
        addressDto.setStreet(address.getStreet());
        addressDto.setUnitNumber(address.getUnitNumber());
        addressDto.setPostCode(address.getPostCode());
        return addressDto;
    }

    public static Address addressDtoToAddress(AddressDto addressDto) {
        if (addressDto == null) {
            return null;
        }
        Address address = new Address();
        address.setCountry(addressDto.getCountry());
        address.setCity(addressDto.getCity());
        address.setStreet(addressDto.getStreet());
        address.setUnitNumber(addressDto.getUnitNumber());
        address.setPostCode(addressDto.getPostCode());
        return address;
    }
}
