package com.example.shoppingApplicationRest.dto.userAuthDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthRequestDto {

    private String email;
    private String password;
}
