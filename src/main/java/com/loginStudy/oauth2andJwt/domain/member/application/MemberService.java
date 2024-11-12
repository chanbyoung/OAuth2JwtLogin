package com.loginStudy.oauth2andJwt.domain.member.application;

import com.loginStudy.oauth2andJwt.domain.image.application.ImageService;
import com.loginStudy.oauth2andJwt.domain.image.entity.Image;
import com.loginStudy.oauth2andJwt.domain.member.dao.MemberRepository;
import com.loginStudy.oauth2andJwt.domain.member.dto.req.MemberAdditionalSetupReqDto;
import com.loginStudy.oauth2andJwt.domain.member.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ImageService imageService;

    @Transactional
    public void setupProfile(String memberAccount, MemberAdditionalSetupReqDto setupDto) throws IOException {
        Member member = memberRepository.findByAccount(memberAccount)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 닉네임 설정 및 일반 회원으로 변경
        member.updateNicknameAndRole(setupDto.getNickname());

        // 프로필 이미지 저장 및 설정
        MultipartFile profileImage = setupDto.getProfileImage();
        if (profileImage != null && !profileImage.isEmpty()) {
            Image image = imageService.saveImage(profileImage, member);
            member.updateProfileImage(image); // Member에 프로필 이미지 설정
        }

        memberRepository.save(member);
    }
}