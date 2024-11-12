package com.loginStudy.oauth2andJwt.domain.member.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class MemberAdditionalSetupReqDto {
    private String nickname;
    private MultipartFile profileImage;
}
