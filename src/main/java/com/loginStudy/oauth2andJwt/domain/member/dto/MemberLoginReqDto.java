package com.loginStudy.oauth2andJwt.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginReqDto {
    private String account;
    private String password;

}
