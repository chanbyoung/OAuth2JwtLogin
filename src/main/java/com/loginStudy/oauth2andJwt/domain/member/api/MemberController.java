package com.loginStudy.oauth2andJwt.domain.member.api;

import com.loginStudy.oauth2andJwt.domain.member.application.MemberService;
import com.loginStudy.oauth2andJwt.domain.member.dto.req.MemberAdditionalSetupReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
}
