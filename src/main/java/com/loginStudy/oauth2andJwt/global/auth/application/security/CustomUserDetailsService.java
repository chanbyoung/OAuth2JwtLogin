package com.loginStudy.oauth2andJwt.global.auth.application.security;

import com.loginStudy.oauth2andJwt.domain.member.dao.MemberRepository;
import com.loginStudy.oauth2andJwt.domain.member.entity.Member;
import com.loginStudy.oauth2andJwt.global.error.BusinessException;
import com.loginStudy.oauth2andJwt.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * '인증 관련 정보' 를 제공하는 User 클래스 반환
     * @param username 회원의 아이디(account)
     * @return 조회한 회원 Entity 를 UserDetail 객체로 변환
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByAccount(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new BusinessException(username, "account", ErrorCode.MEMBER_ACCOUNT_NOT_FOUND));
    }

    private CustomUserDetails createUserDetails(Member member) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
                "ROLE_" + member.getRole().toString());

        return CustomUserDetails.builder()
                .account(member.getAccount())
                .password(member.getPassword())
                .authorities(Collections.singleton(grantedAuthority)) // 권한 설정
                .build();
    }
}
