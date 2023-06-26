package com.example.shoppingApplicationRest.dto.userAuthDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthResponseDto {

    private String token;
}
