package am.shoppingCommon.shoppingApplication.dto.userDto;

import am.shoppingCommon.shoppingApplication.entity.Gender;
import am.shoppingCommon.shoppingApplication.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Ashot Simonyan on 08.06.23.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDto {

    private int id;
    private String name;
    private String surname;
    private String email;
    private Gender gender;
    private String profilePic;
    private Role role;
}
