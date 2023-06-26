package am.shoppingCommon.shoppingApplication.mapper;



import am.shoppingCommon.shoppingApplication.dto.addressDto.AddressDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.*;
import am.shoppingCommon.shoppingApplication.entity.Address;
import am.shoppingCommon.shoppingApplication.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashot Simonyan on 08.06.23.
 */

public class UserMapper {
    public static User map(CreateUserRequestDto dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return user;
    }

    public static User userRegisterDtoToUser(UserRegisterDto userRegisterDto) {
        if (userRegisterDto == null) {
            return null;
        }
        User user = new User();
        user.setName(userRegisterDto.getName());
        user.setSurname(userRegisterDto.getSurname());
        user.setEmail(userRegisterDto.getEmail());
        user.setPassword(userRegisterDto.getPassword());
        user.setGender(userRegisterDto.getGender());
        return user;
    }

    public static User userShortDtoToUser(UserShortDto userShortDto) {
        if (userShortDto == null) {
            return null;
        }
        User user = new User();
        user.setId(userShortDto.getId());
        user.setName(userShortDto.getName());
        user.setSurname(userShortDto.getSurname());
        user.setEmail(userShortDto.getEmail());
        user.setGender(userShortDto.getGender());
        return user;
    }

    public static User userDtoToUser(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setEmail(userDto.getEmail());
        user.setGender(userDto.getGender());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setProfilePic(userDto.getProfilePic());
        user.setRole(userDto.getRole());
        user.setGender(userDto.getGender());
        List<AddressDto> addresses = userDto.getAddresses();
        List<Address> addresses1 = new ArrayList<>();
        for (AddressDto address : addresses) {
            addresses1.add(AddressMapper.addressDtoToAddress(address));
        }
        user.setAddresses(addresses1);
        return user;
    }
    public static User userUpdateDtoToUser(UserUpdateDto userUpdateDto) {
        if (userUpdateDto == null) {
            return null;
        }
        User user = new User();
        user.setId(userUpdateDto.getId());
        user.setName(userUpdateDto.getName());
        user.setSurname(userUpdateDto.getSurname());
        user.setEmail(userUpdateDto.getEmail());
        user.setPhoneNumber(userUpdateDto.getPhoneNumber());
        user.setProfilePic(userUpdateDto.getProfilePic());
        return user;
    }

    public static UserRegisterDto userToUserRegisterDto(User user) {
        if (user == null) {
            return null;
        }
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setName(user.getName());
        userRegisterDto.setSurname(user.getSurname());
        userRegisterDto.setEmail(user.getEmail());
        userRegisterDto.setPassword(user.getPassword());
        userRegisterDto.setGender(user.getGender());
        return userRegisterDto;
    }

    public static UserShortDto userToUserShortDto(User user) {
        if (user == null) {
            return null;
        }
        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setId(user.getId());
        userShortDto.setName(user.getName());
        userShortDto.setSurname(user.getSurname());
        userShortDto.setEmail(user.getEmail());
        userShortDto.setGender(user.getGender());
        return userShortDto;
    }

    public static UserDto userToUserDto(User user) {
        if (user == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setSurname(user.getSurname());
        userDto.setEmail(user.getEmail());
        userDto.setGender(user.getGender());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setProfilePic(user.getProfilePic());
        userDto.setRole(user.getRole());
        userDto.setGender(user.getGender());
        List<Address> addresses = user.getAddresses();
        List<AddressDto> addressDtos = new ArrayList<>();
        for (Address address : addresses) {
            addressDtos.add(AddressMapper.addressToAddressDto(address));
        }
        userDto.setAddresses(addressDtos);
        return userDto;
    }

//    public static User currentUserToUser(CurrentUser currentUser) {
//        if (currentUser != null) {
//            return currentUser.getUser();
//        }
//        return null;
//    }
    public static List<UserDto> userDtoListMap(List<User> userList){
        if (userList == null){
            return null;
        }
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userList) {
            userDtoList.add(UserMapper.userToUserDto(user));
        }
        return userDtoList;
    }

    public static User createUserRequestDtoMap(CreateUserRequestDto createUserRequestDto) {
        if (createUserRequestDto == null){
            return null;
        }
        User user = new User();
        user.setName(createUserRequestDto.getName());
        user.setSurname(createUserRequestDto.getSurname());
        user.setEmail(createUserRequestDto.getEmail());
        user.setPassword(createUserRequestDto.getPassword());
        return user;
    }
}
