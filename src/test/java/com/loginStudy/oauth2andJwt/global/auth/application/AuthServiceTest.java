package com.loginStudy.oauth2andJwt.global.auth.application;

import com.loginStudy.oauth2andJwt.domain.member.dao.MemberRepository;
import com.loginStudy.oauth2andJwt.domain.member.dto.MemberSignUpReqDto;
import com.loginStudy.oauth2andJwt.domain.member.entity.Member;
import com.loginStudy.oauth2andJwt.global.error.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private Member member;
    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .account("testId12")
                .password("testPassword12@")
                .build();
    }

    @Test
    @DisplayName("회원가입이 정상적으로 성공하여, 생성된 Id를 반환한다.")
    void signup() {
        // given
        MemberSignUpReqDto req = MemberSignUpReqDto.builder()
                .account("testId12")
                .password("testPassword12@")
                .passwordConfirm("testPassword12@")
                .build();

        given(memberRepository.findByAccount("testId12")).willReturn(Optional.empty());
        given(memberRepository.save(any())).willReturn(member);

        //when
        Long memberId = authService.signup(req);

        //then
        assertThat(memberId).isEqualTo(1L);
    }

    @Test
    @DisplayName("아이디가 중복일 경우 예외 발생.")
    void checkDuplicateAccount() {
        // given
        MemberSignUpReqDto req = MemberSignUpReqDto.builder()
                .account("testId12")
                .password("testPassword12@")
                .passwordConfirm("testPassword12@")
                .build();
        given(memberRepository.findByAccount("testId12")).willReturn(Optional.of(member));

        //then
        assertThrows(BusinessException.class,
                () -> authService.checkDuplicateAccount(req.getAccount()));
    }
}