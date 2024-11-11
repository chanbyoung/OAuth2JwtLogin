package com.loginStudy.oauth2andJwt.global.auth.application.security;

import com.loginStudy.oauth2andJwt.global.dto.response.AuthResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOauth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // createAuthResponse 호출하여 AuthResponseDto 생성
        AuthResponseDto authResponse = jwtTokenProvider.createAuthResponse(authentication);
        // 쿠키 설정 메소드 호출 (만료 시간을 5분으로 설정)
        setTokenCookie(response, "accessToken", authResponse.getAccessToken());
        setTokenCookie(response, "refreshToken", authResponse.getRefreshToken());

        response.sendRedirect("http://localhost:3000/success-page");
    }
    private void setTokenCookie(HttpServletResponse response, String name, String token) {
        Cookie cookie = new Cookie(name, token);
        cookie.setSecure(false);  // HTTPS 환경에서 설정
        cookie.setPath("/");
        cookie.setMaxAge(300);
        //        cookie.setHttpOnly(true); -> 프론트에서 로컬 스토리지에 저장하지 않고, 쿠키로만 토큰을 주고 받는 경우

        response.addCookie(cookie);
        log.info("쿠키 설정 완료 - Name: {}, Value: {}", name, token);

    }

}

