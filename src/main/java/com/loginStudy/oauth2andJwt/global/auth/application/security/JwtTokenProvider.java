package com.loginStudy.oauth2andJwt.global.auth.application.security;

import com.loginStudy.oauth2andJwt.global.dto.RefreshTokenInfoDto;
import com.loginStudy.oauth2andJwt.global.dto.response.AuthResponseDto;
import com.loginStudy.oauth2andJwt.global.error.BusinessException;
import com.loginStudy.oauth2andJwt.global.error.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    private final Key key;
    private final RedisTokenStore redisTokenStore;
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 30; // 30분
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일
    @Autowired
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, RedisTokenStore redisTokenStore) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.redisTokenStore = redisTokenStore;
    }
    /**
     * Authentication 객체로부터 Access 토큰, Refresh 토큰 생성 및 AuthResponse 반환
     */
    public AuthResponseDto createAuthResponse(Authentication authentication) {
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessToken = createToken(authentication.getName(), roles, ACCESS_TOKEN_EXPIRATION);
        String refreshToken = createToken(authentication.getName(), null, REFRESH_TOKEN_EXPIRATION);

        // Redis에 Refresh 토큰 저장을 위해 RefreshTokenInfoDto 생성
        RefreshTokenInfoDto refreshTokenInfoDto = new RefreshTokenInfoDto(
                authentication.getName(),
                refreshToken,
                roles
        );
        // Redis에 Refresh 토큰 저장
        redisTokenStore.storeRefreshToken(refreshTokenInfoDto);

        return new AuthResponseDto(accessToken, refreshToken);
    }

    // 공통 토큰 생성 로직
    private String createToken(String userId, String roles, long expiration) {
        Claims claims = Jwts.claims().setSubject(userId);
        if (roles != null) {
            claims.put("roles", roles);
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    /**
     * Refresh 토큰을 이용한 Access 토큰 갱신
     */
    public AuthResponseDto refreshAccessToken(String refreshToken) {
        // 1. 토큰 유효성 검사
        if (!validateToken(refreshToken)) {
            throw new BusinessException(refreshToken, "refreshToken", ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. 토큰에서 사용자 ID 추출
        String userId = getUserIdFromToken(refreshToken);

        // 3. Redis에서 Refresh 토큰 유효성 검사
        if (!redisTokenStore.isValidRefreshToken(userId, refreshToken)) {
            throw new BusinessException(refreshToken, "refreshToken", ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 4. Redis에서 사용자 권한 정보 추출
        String authorities = redisTokenStore.getAuthorities(userId);

        // 5. 새로운 Access 토큰 생성
        String newAccessToken = createToken(userId, authorities, ACCESS_TOKEN_EXPIRATION);

        // 6. 새로운 AuthResponseDto 반환 (기존 Refresh 토큰 유지)
        return new AuthResponseDto(newAccessToken, refreshToken);
    }


    // 토큰에서 사용자 ID 추출
    public String getUserIdFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 토큰에서 사용자 정보 추출 후 Authentication 객체 생성
     * @param token JWT 토큰
     * @return Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        // 1. 토큰에서 사용자 정보 추출
        Claims claims = parseClaims(token);
        String account = claims.getSubject();
        String roles = claims.get("roles", String.class);

        // 2. 권한 정보 설정
        Collection<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 3. CustomUserDetails 객체 생성
        CustomUserDetails customUserDetails = CustomUserDetails.builder()
                .account(account)
                .password("") // 비밀번호는 빈 문자열로 설정
                .authorities(authorities)
                .build();

        // 4. Authentication 객체 반환
        return new UsernamePasswordAuthenticationToken(customUserDetails, token, authorities);
    }

    /**
     * 토큰에서 만료 시간 가져오기
     *
     * @param token JWT 토큰
     * @return 토큰의 남은 만료 시간 (밀리초)
     */
    public long getExpiration(String token) {
        Claims claims = parseClaims(token);
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Claims 파싱
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
