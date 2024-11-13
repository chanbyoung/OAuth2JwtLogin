package com.loginStudy.oauth2andJwt.global.auth.application.security;

import com.loginStudy.oauth2andJwt.global.dto.response.AuthResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class CustomOauth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenStore redisTokenStore;
    private static final String TEMP_TOKEN_NAME = "tempToken";
    private static final String GUEST = "isGuest";
    private static final String USER_ACCOUNT = "account";
    private static final String SUCCESS_URL = "http://localhost:3000/success-page";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomUserDetails oAuth2User = (CustomUserDetails) authentication.getPrincipal();

        // 최종 액세스 및 리프레시 토큰 생성
        AuthResponseDto authResponse = jwtTokenProvider.createAuthResponse(authentication);

        // Redis에 authResponse 저장 및 임시 토큰 발급
        String tempToken = redisTokenStore.generateTemporaryToken(authResponse);
        String account = oAuth2User.getAccount();
        boolean isGuest = oAuth2User.isGuest();
        // 성공 URL에 쿼리 파라미터로 임시 토큰과 isGuest 정보를 전달
        String redirectUrl = UriComponentsBuilder.fromUriString(SUCCESS_URL)
                .queryParam(TEMP_TOKEN_NAME, tempToken)
                .queryParam(GUEST, isGuest)
                .queryParam(USER_ACCOUNT, account)
                .build().toUriString();

        response.sendRedirect(redirectUrl); // 프론트엔드로 리다이렉트
    }
}

