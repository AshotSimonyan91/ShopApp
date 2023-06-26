package com.example.shoppingApplicationRest.endpoint;


import am.shoppingCommon.shoppingApplication.dto.userDto.CreateUserRequestDto;
import am.shoppingCommon.shoppingApplication.dto.userDto.UserDto;
import am.shoppingCommon.shoppingApplication.entity.Role;
import am.shoppingCommon.shoppingApplication.entity.User;
import am.shoppingCommon.shoppingApplication.mapper.UserMapper;
import am.shoppingCommon.shoppingApplication.repository.UserRepository;
import com.example.shoppingApplicationRest.dto.userAuthDto.UserAuthRequestDto;
import com.example.shoppingApplicationRest.dto.userAuthDto.UserAuthResponseDto;
import com.example.shoppingApplicationRest.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserEndpoint {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil tokenUtil;

    @PostMapping("/auth")
    public ResponseEntity<UserAuthResponseDto> auth(@RequestBody UserAuthRequestDto userAuthRequestDto) {
        Optional<User> byEmail = userRepository.findByEmail(userAuthRequestDto.getEmail());
        if (byEmail.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = byEmail.get();
        if (!passwordEncoder.matches(userAuthRequestDto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = tokenUtil.generateToken(userAuthRequestDto.getEmail());
        return ResponseEntity.ok(new UserAuthResponseDto(token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody CreateUserRequestDto createUserRequestDto) {
        Optional<User> byEmail = userRepository.findByEmail(createUserRequestDto.getEmail());
        if (byEmail.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        User user = UserMapper.createUserRequestDtoMap(createUserRequestDto);
        user.setPassword(passwordEncoder.encode(createUserRequestDto.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        return ResponseEntity.ok(UserMapper.userToUserDto(user));
    }

}
