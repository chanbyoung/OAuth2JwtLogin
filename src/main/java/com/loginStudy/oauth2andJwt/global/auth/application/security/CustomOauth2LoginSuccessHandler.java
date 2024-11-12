package com.loginStudy.oauth2andJwt.global.auth.application.security;

import com.loginStudy.oauth2andJwt.global.dto.response.AuthResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class CustomOauth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;

    // 상수로 선언
    private static final String ACCESS_TOKEN_NAME = "accessToken";
    private static final String REFRESH_TOKEN_NAME = "refreshToken";
    private static final String SIGN_UP_URL = "http://localhost:3000/signUp/setUp";
    private static final String SUCCESS_URL = "http://localhost:3000/success-page";
    private static final int TOKEN_EXPIRATION = 300;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // createAuthResponse 호출하여 AuthResponseDto 생성
        AuthResponseDto authResponse = jwtTokenProvider.createAuthResponse(authentication);

        // 쿠키 설정 메소드 호출 (만료 시간을 5분으로 설정)
        setTokenCookie(response, ACCESS_TOKEN_NAME, authResponse.getAccessToken());
        setTokenCookie(response, REFRESH_TOKEN_NAME, authResponse.getRefreshToken());

        CustomUserDetails oAuth2User = (CustomUserDetails) authentication.getPrincipal();
        // 처음 로그인 한 회원의 경우 추가 설정으로 리다이렉트
        if (oAuth2User.isGuest()) {
            response.sendRedirect(SIGN_UP_URL);
        } else {
            response.sendRedirect(SUCCESS_URL);
        }
    }

    private void setTokenCookie(HttpServletResponse response, String name, String token) {
        Cookie cookie = new Cookie(name, token);
        cookie.setSecure(false);  // HTTPS 환경에서는 true로 설정
        cookie.setPath("/");
        cookie.setMaxAge(TOKEN_EXPIRATION);
        //        cookie.setHttpOnly(true); -> 프론트에서 로컬 스토리지에 저장하지 않고, 쿠키로만 토큰을 주고 받는 경우
        response.addCookie(cookie);
    }
}

