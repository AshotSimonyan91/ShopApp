package am.shoppingCommon.shoppingApplication.dto.addressDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Ashot Simonyan on 09.06.23.
 */

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AddressDto {

    private int id;
    private String country;
    private String city;
    private String street;
    private String unitNumber;
    private String postCode;
}
