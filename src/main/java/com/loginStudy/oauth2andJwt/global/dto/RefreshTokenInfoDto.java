package com.loginStudy.oauth2andJwt.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenInfoDto {
    private String userAccount;
    private String refreshToken;
    private String authorities;
}
