package com.loginStudy.oauth2andJwt.domain.member.api;

import com.loginStudy.oauth2andJwt.domain.member.application.MemberService;
import com.loginStudy.oauth2andJwt.domain.member.dto.rep.MemberProfileRepDto;
import com.loginStudy.oauth2andJwt.domain.member.dto.req.MemberAdditionalSetupReqDto;
import com.loginStudy.oauth2andJwt.global.auth.application.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
@Slf4j
@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 추가 설정 API
    @PostMapping("/{account}/additional-setup")
    public ResponseEntity<String> additionalSetup(
            @PathVariable String account,
            @ModelAttribute MemberAdditionalSetupReqDto setupDto
    ) throws IOException {
        memberService.setupProfile(account, setupDto);
        return ResponseEntity.ok("추가 설정이 완료되었습니다.");
    }
    @GetMapping("/profile")
    public ResponseEntity<MemberProfileRepDto> getMemberProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String memberAccount = userDetails.getUsername();
        MemberProfileRepDto profile = memberService.getMemberProfile(memberAccount);
        return ResponseEntity.ok(profile);
    }
}
