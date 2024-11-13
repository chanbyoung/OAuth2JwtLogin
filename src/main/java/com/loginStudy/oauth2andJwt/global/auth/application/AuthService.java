package com.loginStudy.oauth2andJwt.global.auth.application;

import com.loginStudy.oauth2andJwt.domain.member.dao.MemberRepository;
import com.loginStudy.oauth2andJwt.domain.member.dto.req.MemberLoginReqDto;
import com.loginStudy.oauth2andJwt.domain.member.dto.req.MemberSignUpReqDto;
import com.loginStudy.oauth2andJwt.domain.member.entity.Member;
import com.loginStudy.oauth2andJwt.global.auth.application.security.JwtTokenProvider;
import com.loginStudy.oauth2andJwt.global.auth.application.security.RedisTokenStore;
import com.loginStudy.oauth2andJwt.global.dto.response.AuthResponseDto;
import com.loginStudy.oauth2andJwt.global.error.BusinessException;
import com.loginStudy.oauth2andJwt.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenStore redisTokenStore;
    // SecurityConfig 에서 @Bean 으로 등록된 PasswordEncoder 와 AuthenticationManager 주입
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    @Transactional
    public Long signup(MemberSignUpReqDto reqDto) {
        checkDuplicateAccount(reqDto.getAccount());

        Member member = reqDto.toEntity(passwordEncoder);

        Member createdMember = memberRepository.save(member);

        return createdMember.getId();
    }
    // 로그인 처리 및 AuthResponse 생성
    @Transactional
    public AuthResponseDto login(MemberLoginReqDto memberLoginReqDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(memberLoginReqDto.getAccount(), memberLoginReqDto.getPassword())
        );

        // JwtTokenProvider에서 Access, Refresh 토큰 생성 및 AuthResponse 반환
        return jwtTokenProvider.createAuthResponse(authentication);
    }
    @Transactional
    public void logout(String jwtToken) {
        long accessTokenExpiration = jwtTokenProvider.getExpiration(jwtToken);
        String userId = jwtTokenProvider.getUserIdFromToken(jwtToken);

        redisTokenStore.logoutTokens(jwtToken, accessTokenExpiration, userId);
    }

    /**
     * Refresh 토큰을 이용한 Access 토큰 갱신
     *
     * @param refreshToken 클라이언트로부터 전달받은 Refresh 토큰
     * @return AuthResponseDto 새로 생성된 Access 토큰과 기존의 Refresh 토큰
     */
    public AuthResponseDto refreshAccessToken(String refreshToken) {
        // JwtTokenProvider 에서 Refresh 토큰 유효성 검사 및 Access 토큰 재발급
        try {
            return jwtTokenProvider.refreshAccessToken(refreshToken);
        } catch (BusinessException e) {
            throw new BusinessException(refreshToken, "refreshToken", ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
    /**
     * 회원 아이디 중복 확인
     *
     * @param account 확인할 회원 아이디
     */
    public void checkDuplicateAccount(String account) {
        if (memberRepository.findByAccount(account).isPresent()) {
            throw new BusinessException(account, "account", ErrorCode.MEMBER_ACCOUNT_DUPLICATE);
        }
    }

    /**
     * 임시 토큰을 사용하여 Redis에서 인증 응답 데이터를 조회합니다.
     *
     * @param tempToken 임시 토큰 (프론트엔드에서 받은 tempToken)
     * @return AuthResponseDto 인증에 필요한 Access, Refresh 토큰
     *         조회된 데이터가 없을 경우 null을 반환합니다.
     */
    public AuthResponseDto retrieveAuthResponse(String tempToken) {
        return redisTokenStore.retrieveAuthResponse(tempToken);
    }

}
