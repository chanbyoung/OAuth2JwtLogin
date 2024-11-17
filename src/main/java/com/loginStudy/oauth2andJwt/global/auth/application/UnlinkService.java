package com.loginStudy.oauth2andJwt.global.auth.application;

import com.loginStudy.oauth2andJwt.domain.member.entity.Member;
import com.loginStudy.oauth2andJwt.global.auth.application.security.RedisTokenStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
@Slf4j
@Service
@RequiredArgsConstructor
public class UnlinkService {
    private final RestTemplate restTemplate;
    private final RedisTokenStore redisTokenStore;
    // 카카오 앱 키 (yml에서 주입받기)
    @Value("${kakao.admin-key}")
    private String kakaoAdminKey;
    private static final String KAKAO_UNLINK_URL = "https://kapi.kakao.com/v1/user/unlink";
    private static final String GOOGLE_REVOKE_URL = "https://oauth2.googleapis.com/revoke";


    // 카카오 연결 끊기
    private void unlinkKakao(String providerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoAdminKey); // Admin Key 사용
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", providerId);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(KAKAO_UNLINK_URL, request, String.class);
            log.info("카카오 연결 끊기 성공: 사용자 ID = {}", providerId);
        } catch (Exception e) {
            log.error("카카오 연결 끊기 실패: 사용자 ID = {}", providerId, e);
            throw new RuntimeException("카카오 연결 끊기 실패", e);
        }
    }

    // 구글 연결 끊기
    private void unlinkGoogle(String account) {
        String refreshToken = redisTokenStore.fetchAndDeleteSocialRefreshToken(account);
        String url = GOOGLE_REVOKE_URL + "?token=" + refreshToken;

        try {
            restTemplate.postForEntity(url, null, String.class);
            log.info("구글 연결 끊기 성공: Refresh Token = {}", refreshToken);
        } catch (Exception e) {
            log.error("구글 연결 끊기 실패: Refresh Token = {}", refreshToken, e);
            throw new RuntimeException("구글 연결 끊기 실패", e);
        }
    }

    // 소셜 연결 끊기
    public void unlink(Member member) {
        switch (member.getProvider().toLowerCase()) {
            case "kakao":
                unlinkKakao(member.getProviderId());
                break;
            case "google":
                unlinkGoogle(member.getAccount());
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 소셜 제공자입니다.");
        }
    }
}
