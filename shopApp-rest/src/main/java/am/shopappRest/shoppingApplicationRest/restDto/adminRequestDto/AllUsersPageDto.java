package am.shopappRest.shoppingApplicationRest.restDto.adminRequestDto;

import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllUsersPageDto {
    private UserDto currentUser;
    private List<UserDto> userDtoList;
}
