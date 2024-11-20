package com.loginStudy.oauth2andJwt.global.config.security.filter;

import com.loginStudy.oauth2andJwt.global.config.redis.RedisTokenStore;
import com.loginStudy.oauth2andJwt.global.config.security.JwtTokenProvider;
import com.loginStudy.oauth2andJwt.global.error.ErrorCode;
import com.loginStudy.oauth2andJwt.global.error.TokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenStore redisTokenStore;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String token = extractToken(httpRequest);
        if (token != null) {
            authenticateToken(token); // 인증 로직
        }

        // 다음 필터로 요청 전달
        chain.doFilter(request, response);
    }

    /**
     * 요청 헤더에서 JWT 토큰을 추출하는 메서드
     * @param request HTTP 요청
     * @return 추출된 JWT 토큰 (없으면 null 반환)
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) ?
                bearerToken.substring(7) : null;
    }

    /**
     * 토큰의 유효성을 검증하고 블랙리스트에 포함되지 않으면 인증 객체를 설정하는 메서드
     * @param token JWT 토큰
     */
    private void authenticateToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new TokenException(ErrorCode.AUTHENTICATION_FAILED);
        }
        if (isTokenBlacklisted(token)) {
            throw new TokenException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Redis 블랙리스트에서 토큰의 존재 여부를 확인하는 메서드
     * @param token JWT 토큰
     * @return 토큰이 블랙리스트에 있으면 true 반환
     */
    private boolean isTokenBlacklisted(String token) {
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        return Boolean.TRUE.equals(redisTokenStore.isValidRefreshToken(userId, token));
    }

}
