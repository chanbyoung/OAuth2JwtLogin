package com.loginStudy.oauth2andJwt.global.auth.application;

import com.loginStudy.oauth2andJwt.domain.member.dao.MemberRepository;
import com.loginStudy.oauth2andJwt.domain.member.dto.MemberSignUpReqDto;
import com.loginStudy.oauth2andJwt.domain.member.entity.Member;
import com.loginStudy.oauth2andJwt.global.error.BusinessException;
import com.loginStudy.oauth2andJwt.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    // 시큐리티 설정에서 가져옴
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signup(MemberSignUpReqDto reqDto) {
        checkDuplicateAccount(reqDto.getAccount());

        Member member = reqDto.toEntity(passwordEncoder);

        Member createdMember = memberRepository.save(member);

        return createdMember.getId();
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

}
