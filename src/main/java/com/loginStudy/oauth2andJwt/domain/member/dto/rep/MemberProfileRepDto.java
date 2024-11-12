package com.loginStudy.oauth2andJwt.domain.member.dto.rep;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberProfileRepDto {
    private String nickname;
    private String profileImageUrl;
}
