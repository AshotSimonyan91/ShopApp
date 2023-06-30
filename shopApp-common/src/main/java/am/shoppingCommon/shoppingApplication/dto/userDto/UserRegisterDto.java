package am.shoppingCommon.shoppingApplication.dto.userDto;

import am.shoppingCommon.shoppingApplication.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by Ashot Simonyan on 08.06.23.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDto {

    @NotNull
    @Size(min = 2,max = 15,message = "Name length it should be min 2 max 15 characters")
    private String name;
    @NotNull
    @Size(min = 2,max = 15,message = "Surname length it should be min 2 max 15 characters")
    private String surname;
    @Email(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
            message = "Email is no valid")
    private String email;
//    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$",
//            message = "Should be min 8 character,include digit and capital letter")
    private String password;
    @NotNull(message = "Gender is required")
    private Gender gender;
}
