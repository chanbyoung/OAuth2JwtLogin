package com.loginStudy.oauth2andJwt.global.auth.application.security;

import com.loginStudy.oauth2andJwt.global.dto.RefreshTokenInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisTokenStore {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Refresh 토큰과 사용자 정보를 Redis에 저장
     */
    public void storeRefreshToken(RefreshTokenInfoDto tokenData) {
        try {
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();

            // RefreshTokenData 에서 데이터를 맵 형태로 변환
            HashMap<String, Object> tokenDataMap = createTokenDataMap(tokenData);

            // 해시로 데이터 저장
            hashOperations.putAll(tokenData.getUserAccount(), tokenDataMap);

            // 만료 시간 설정 (1주일)
            boolean isExpireSet = Boolean.TRUE.equals(redisTemplate.expire(tokenData.getUserAccount(), 7, TimeUnit.DAYS));
            if (!isExpireSet) {
                log.warn("TTL 설정에 실패했습니다. userAccount: {}", tokenData.getUserAccount());
            }
        } catch (RedisConnectionFailureException e) {
            log.error("Redis 연결 실패", e);
        } catch (RedisSystemException e) {
            log.error("Redis 시스템 예외 발생", e);
        } catch (Exception e) {
            log.error("Redis에 데이터를 저장 중 예기치 못한 오류 발생", e);
        }
    }
    /**
     * Refresh 토큰이 유효한지 확인
     */
    public boolean isValidRefreshToken(String userAccount, String refreshToken) {
        try {
            String storedRefreshToken = (String) redisTemplate.opsForHash().get(userAccount, "refreshToken");
            return storedRefreshToken != null && storedRefreshToken.equals(refreshToken);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis 연결 실패", e);
            return false;
        } catch (Exception e) {
            log.error("Refresh 토큰 검증 중 예기치 못한 오류 발생", e);
            return false;
        }
    }
    /**
     * Refresh 토큰 저장을 위한 데이터 맵 생성
     */
    private HashMap<String, Object> createTokenDataMap(RefreshTokenInfoDto tokenData) {
        HashMap<String, Object> tokenDataMap = new HashMap<>();
        tokenDataMap.put("refreshToken", tokenData.getRefreshToken());
        tokenDataMap.put("authorities", tokenData.getAuthorities());
        return tokenDataMap;
    }

    /**
     * Redis에서 사용자 권한 정보 가져오기
     */
    public String getAuthorities(String userAccount) {
        try {
            // Redis에서 userAccount에 해당하는 "authorities" 필드를 가져옴
            return (String) redisTemplate.opsForHash().get(userAccount, "authorities");
        } catch (RedisConnectionFailureException e) {
            log.error("Redis 연결 실패", e);
            return null;
        } catch (Exception e) {
            log.error("사용자 권한 정보를 가져오는 중 예기치 못한 오류 발생", e);
            return null;
        }
    }
}
