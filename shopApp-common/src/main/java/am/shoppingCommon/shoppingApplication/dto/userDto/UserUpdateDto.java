package am.shoppingCommon.shoppingApplication.dto.userDto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    private int id;

    @Size(min = 0,
            max = 15,
            message = "Name length it should be min 2 max 15 characters")
    private String name;

    @Size(min = 0,
           max = 15,
            message = "Surname length it should be min 2 max 15 characters")
    private String surname;

    @Email(regexp = "^(?:[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+)?$",
            message = "Email is not valid")
    private String email;

    private String phoneNumber;
    private String profilePic;
}
