package am.shoppingCommon.shoppingApplication.mapper;


import am.shoppingCommon.shoppingApplication.dto.addressDto.AddressDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.*;
import am.shoppingCommon.shoppingApplication.entity.Address;
import am.shoppingCommon.shoppingApplication.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

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
        return User.builder()
                .name(dto.getName())
                .surname(dto.getSurname())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .build();
    }

    public static User userRegisterDtoToUser(UserRegisterDto userRegisterDto) {
        if (userRegisterDto == null) {
            return null;
        }
        return User.builder()
                .name(userRegisterDto.getName())
                .surname(userRegisterDto.getSurname())
                .email(userRegisterDto.getEmail())
                .password(userRegisterDto.getPassword())
                .gender(userRegisterDto.getGender())
                .build();
    }

    public static UserDto userRegisterDtoToUerDto(UserRegisterDto userRegisterDto) {
        if (userRegisterDto == null) {
            return null;
        }
        return UserDto.builder()
                .name(userRegisterDto.getName())
                .surname(userRegisterDto.getSurname())
                .email(userRegisterDto.getEmail())
                .gender(userRegisterDto.getGender())
                .build();
    }

    public static User userShortDtoToUser(UserShortDto userShortDto) {
        if (userShortDto == null) {
            return null;
        }
        return User.builder()
                .id(userShortDto.getId())
                .name(userShortDto.getName())
                .surname(userShortDto.getSurname())
                .email(userShortDto.getEmail())
                .gender(userShortDto.getGender())
                .build();
    }

    public static User userDtoToUser(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        List<AddressDto> addresses = userDto.getAddresses();
        List<Address> addressesNew = new ArrayList<>();
        for (AddressDto address : addresses) {
            addressesNew.add(AddressMapper.addressDtoToAddress(address));
        }
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .surname(userDto.getSurname())
                .email(userDto.getEmail())
                .gender(userDto.getGender())
                .phoneNumber(userDto.getPhoneNumber())
                .profilePic(userDto.getProfilePic())
                .role(userDto.getRole())
                .enabled(userDto.isEnabled())
                .token(userDto.getToken())
                .addresses(addressesNew)
                .build();
    }

    public static User userUpdateDtoToUser(UserUpdateDto userUpdateDto) {
        if (userUpdateDto == null) {
            return null;
        }
        return User.builder()
                .id(userUpdateDto.getId())
                .name(userUpdateDto.getName())
                .surname(userUpdateDto.getSurname())
                .email(userUpdateDto.getEmail())
                .phoneNumber(userUpdateDto.getPhoneNumber())
                .profilePic(userUpdateDto.getProfilePic())
                .build();
    }

    public static UserRegisterDto userToUserRegisterDto(User user) {
        if (user == null) {
            return null;
        }
        return UserRegisterDto.builder()
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .password(user.getPassword())
                .gender(user.getGender())
                .build();
    }

    public static UserShortDto userToUserShortDto(User user) {
        if (user == null) {
            return null;
        }
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .profilePic(user.getProfilePic())
                .role(user.getRole())
                .gender(user.getGender())
                .build();
    }

    public static UserDto userToUserDto(User user) {
        if (user == null) {
            return null;
        }
        List<Address> addresses = user.getAddresses();
        List<AddressDto> addressDtos = new ArrayList<>();
        if (addresses != null) {
            for (Address address : addresses) {
                addressDtos.add(AddressMapper.addressToAddressDto(address));
            }
        }
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .profilePic(user.getProfilePic())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .token(user.getToken())
                .addresses(addressDtos)
                .build();
    }

    public static List<UserDto> userDtoListMap(List<User> userList) {
        if (userList == null) {
            return null;
        }
        return userList.stream()
                .map(UserMapper::userToUserDto)
                .toList();
    }

    public static User createUserRequestDtoMap(CreateUserRequestDto createUserRequestDto) {
        if (createUserRequestDto == null) {
            return null;
        }
        return User.builder()
                .name(createUserRequestDto.getName())
                .surname(createUserRequestDto.getSurname())
                .email(createUserRequestDto.getEmail())
                .password(createUserRequestDto.getPassword())
                .build();
    }

    public static Page<UserDto> mapPageToDto(Page<User> users) {
        if (users == null) {
            return null;
        }
        List<UserDto> list = users.getContent().stream()
                .map(UserMapper::userToUserDto)
                .toList();

        return new PageImpl<>(list, users.getPageable(), users.getTotalElements());
    }
}
