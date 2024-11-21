    package com.loginStudy.oauth2andJwt.global.config.security.filter;

    import com.loginStudy.oauth2andJwt.global.config.redis.RedisTokenStore;
    import com.loginStudy.oauth2andJwt.global.config.security.JwtTokenProvider;
    import io.jsonwebtoken.ExpiredJwtException;
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.util.StringUtils;
    import org.springframework.web.filter.OncePerRequestFilter;

    import java.io.IOException;
    @Slf4j
    @RequiredArgsConstructor
    public class JwtAuthenticationFilter extends OncePerRequestFilter {
        private final JwtTokenProvider jwtTokenProvider;
        private final RedisTokenStore redisTokenStore;

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            String token = extractToken(request);

            try {
                if (token != null) {
                    jwtTokenProvider.validateToken(token);
                    if (!isTokenBlacklisted(token)) {
                        Authentication authentication = jwtTokenProvider.getAuthentication(token);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (ExpiredJwtException e) {
                log.warn("만료된 토큰: {}", e.getMessage());
            }

            filterChain.doFilter(request, response);
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
         * Redis 블랙리스트에서 토큰의 존재 여부를 확인하는 메서드
         * @param token JWT 토큰
         * @return 토큰이 블랙리스트에 있으면 true 반환
         */
        private boolean isTokenBlacklisted(String token) {
            String userId = jwtTokenProvider.getUserIdFromToken(token);
            return Boolean.TRUE.equals(redisTokenStore.isValidRefreshToken(userId, token));
        }

    }
