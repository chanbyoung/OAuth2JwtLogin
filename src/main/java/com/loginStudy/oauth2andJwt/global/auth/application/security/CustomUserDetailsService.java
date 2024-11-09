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

    private final MemberRepository memberRepository;

    /**
     * '인증 관련 정보' 를 제공하는 User 클래스 반환
     * @param username 회원의 아이디(account)
     * @return 조회한 회원 Entity 를 UserDetail 객체로 변환
     */
    @Override
    public CustomUserDetails loadUserByUsername(String username) {
        Member member = memberRepository.findByAccount(username)
                .orElseThrow(() -> new BusinessException(username, "account", ErrorCode.MEMBER_ACCOUNT_NOT_FOUND));

        return createUserDetails(member, null);
    }

    /**
     * OAuth2 로그인 시 사용
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // provider에 맞는 이메일 추출
        String email = extractEmail(oAuth2User, provider);


        Member member = memberRepository.findByAccount(email)
                .orElseGet(() -> memberRepository.save(Member.socialMember(email, Role.USER, provider)));

        return createUserDetails(member, oAuth2User.getAttributes());
    }

    private CustomUserDetails createUserDetails(Member member, Map<String, Object> attributes) {
        return CustomUserDetails.builder()
                .account(member.getAccount())
                .password(member.getPassword())
                .authorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_" + member.getRole().toString())))
                .attributes(attributes) // OAuth2일 경우 속성 설정
                .build();
    }
    /**
     * 각 소셜 제공자별로 이메일을 추출하는 메서드
     */
    private String extractEmail(OAuth2User oAuth2User, String provider) {
        return switch (provider) {
            case "kakao" ->
                    (String) ((Map<String, Object>) oAuth2User.getAttributes().get("kakao_account")).get("email");
            case "naver" -> {
                Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes().get("response");
                yield (String) response.get("email");
            }
            case "google" -> (String) oAuth2User.getAttributes().get("email");
            default -> throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인 제공자입니다: " + provider);
        };
    }
}