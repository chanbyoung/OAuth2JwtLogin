package com.loginStudy.oauth2andJwt.global.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshTokenInfoDto {
    private String userAccount;
    private String refreshToken;
    private String authorities;
    private String nickName;
    public static RefreshTokenInfoDto toDto(String userAccount, String refreshToken, String authorities, String nickName) {
        return RefreshTokenInfoDto.builder()
                .userAccount(userAccount)
                .refreshToken(refreshToken)
                .authorities(authorities)
                .nickName(nickName)
                .build();
    }
}
