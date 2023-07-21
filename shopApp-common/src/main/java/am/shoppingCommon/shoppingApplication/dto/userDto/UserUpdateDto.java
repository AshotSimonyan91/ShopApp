package am.shoppingCommon.shoppingApplication.dto.userDto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
