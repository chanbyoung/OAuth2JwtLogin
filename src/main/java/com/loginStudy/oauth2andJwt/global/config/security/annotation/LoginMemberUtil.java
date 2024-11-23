package com.loginStudy.oauth2andJwt.global.config.security.annotation;

import com.loginStudy.oauth2andJwt.global.auth.application.security.CustomUserDetails;
import org.springframework.security.core.AuthenticationException;

/**
 * @LoginMember에서 사용되는 유틸리티 클래스
 */
public class LoginMemberUtil {

    /**
     * 인증된 Principal 객체를 반환하거나 예외를 발생
     *
     * @param principal 인증된 사용자 정보
     * @return memberAccount 사용자 계정
     * @throws AuthenticationException 인증되지 않은 경우
     */
    public static String  getPrincipalOrThrow(Object principal) {
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getAccount(); // 인증된 사용자 반환
        }

        // 인증되지 않았거나 예상치 못한 객체 타입일 경우 예외 발생
        throw new AuthenticationException("Authentication is required") {
        };
    }
}