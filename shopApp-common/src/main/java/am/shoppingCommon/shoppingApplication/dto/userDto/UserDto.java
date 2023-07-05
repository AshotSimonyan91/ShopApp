package am.shoppingCommon.shoppingApplication.dto.userDto;


import am.shoppingCommon.shoppingApplication.dto.addressDto.AddressDto;
import am.shoppingCommon.shoppingApplication.entity.Gender;
import am.shoppingCommon.shoppingApplication.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * Created by Ashot Simonyan on 08.06.23.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private int id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private String profilePic;
    private Role role;
    private Gender gender;
    private boolean enabled;
    private String token;
    private List<AddressDto> addresses;
}
