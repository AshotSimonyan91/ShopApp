package am.shoppingCommon.shoppingApplication.dto.userDto;

import am.shoppingCommon.shoppingApplication.entity.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;



/**
 * Created by Ashot Simonyan on 08.06.23.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterDto {

    @Size(min = 2,max = 15,message = "Name length it should be min 2 max 15 characters")
    @Column(nullable = false)
    private String name;
    @Size(min = 2,max = 15,message = "Surname length it should be min 2 max 15 characters")
    private String surname;
    @Email(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "Email is no valid")
    private String email;
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$",
            message = "Should be min 8 character,include digit and capital letter")
    private String password;
    @Enumerated(EnumType.STRING)
    private Gender gender;
}
