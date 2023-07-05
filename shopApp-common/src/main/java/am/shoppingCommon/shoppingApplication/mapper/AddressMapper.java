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
        return AddressDto.builder()
                .id(address.getId())
                .country(address.getCountry())
                .city(address.getCity())
                .street(address.getStreet())
                .unitNumber(address.getUnitNumber())
                .postCode(address.getPostCode())
                .build();
    }

    public static Address addressDtoToAddress(AddressDto addressDto) {
        if (addressDto == null) {
            return null;
        }
        return Address.builder()
                .country(addressDto.getCountry())
                .city(addressDto.getCity())
                .street(addressDto.getStreet())
                .unitNumber(addressDto.getUnitNumber())
                .postCode(addressDto.getPostCode())
                .build();
    }
}
