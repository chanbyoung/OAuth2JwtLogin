package com.loginStudy.oauth2andJwt.global.auth.application.security;

import com.loginStudy.oauth2andJwt.domain.member.dao.MemberRepository;
import com.loginStudy.oauth2andJwt.domain.member.entity.Member;
import com.loginStudy.oauth2andJwt.domain.member.entity.Role;
import com.loginStudy.oauth2andJwt.global.error.BusinessException;
import com.loginStudy.oauth2andJwt.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService extends DefaultOAuth2UserService implements UserDetailsService {

    // 소셜 제공자 이름은 재사용될 가능성이 크므로 상수로 유지
    private static final String KAKAO = "kakao";
    private static final String GOOGLE = "google";
    private static final String NAVER = "naver";

    private final MemberRepository memberRepository;

    /**
     * '인증 관련 정보' 를 제공하는 User 클래스 반환
     *
     * @param username 회원의 아이디(account)
     * @return 조회한 회원 Entity 를 UserDetail 객체로 변환
     */
    @Override
    public CustomUserDetails loadUserByUsername(String username) {
        Member member = memberRepository.findByAccount(username)
                .orElseThrow(() -> new BusinessException(username, "account",
                        ErrorCode.MEMBER_ACCOUNT_NOT_FOUND));

        return createUserDetails(member, null);
    }

    /**
     * OAuth2 로그인 시 사용
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 소셜 로그인 제공자 (ex: kakao, google, naver)
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // 소셜 고유 ID 및 이메일 추출
        String providerId = String.valueOf(
                extractAttribute(oAuth2User, provider, isGoogleProvider(provider) ? "sub" : "id"));
        String email = (String) extractAttribute(oAuth2User, provider, "email");

        // provider와 providerId를 기준으로 사용자 조회
        Member member = memberRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> memberRepository.save(
                        Member.socialMember(email, Role.GUEST, provider, providerId)));

        // UserDetails 생성 및 반환
        return createUserDetails(member, oAuth2User.getAttributes());
    }

    private CustomUserDetails createUserDetails(Member member, Map<String, Object> attributes) {
        return CustomUserDetails.builder()
                .account(member.getAccount())
                .password(member.getPassword())
                .authorities(Collections.singleton(
                        new SimpleGrantedAuthority("ROLE_" + member.getRole().toString())))
                .attributes(attributes) // OAuth2일 경우 속성 설정
                .build();
    }

    /**
     * 소셜 제공자별로 필요한 속성을 추출하는 메서드
     */
    private Object extractAttribute(OAuth2User oAuth2User, String provider, String attributeKey) {
        return switch (provider.toLowerCase()) {
            case KAKAO -> extractKakaoAttribute(oAuth2User, attributeKey);
            case NAVER -> extractNaverAttribute(oAuth2User, attributeKey);
            case GOOGLE -> oAuth2User.getAttributes().get(attributeKey);
            default -> throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인 제공자입니다: " + provider);
        };
    }

    private Object extractKakaoAttribute(OAuth2User oAuth2User, String attributeKey) {
        if ("email".equals(attributeKey)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes()
                    .get("kakao_account");
            return kakaoAccount.get("email");
        }
        return oAuth2User.getAttributes().get("id");
    }

    private Object extractNaverAttribute(OAuth2User oAuth2User, String attributeKey) {
        Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes()
                .get("response");
        return response.get(attributeKey);
    }

    private boolean isGoogleProvider(String provider) {
        return GOOGLE.equals(provider);
    }
}