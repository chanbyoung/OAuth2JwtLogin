package com.loginStudy.oauth2andJwt.global.auth.api;

import com.loginStudy.oauth2andJwt.domain.member.dto.req.MemberLoginReqDto;
import com.loginStudy.oauth2andJwt.domain.member.dto.req.MemberSignUpReqDto;
import com.loginStudy.oauth2andJwt.global.auth.application.AuthService;
import com.loginStudy.oauth2andJwt.global.dto.request.RefreshTokenRequestDto;
import com.loginStudy.oauth2andJwt.global.dto.response.ApiResDto;
import com.loginStudy.oauth2andJwt.global.dto.response.AuthResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<ApiResDto> signUp(
            @Valid @RequestBody MemberSignUpReqDto reqDto
    ){
        reqDto.validPasswordConfirm(); // 비밀번호 확인

        Long memberId = authService.signup(reqDto);
        return ResponseEntity.status(CREATED)
                .body(ApiResDto.toSuccessForm(memberId));
    }

    @GetMapping("/signUp/exists/account")
    public ResponseEntity<ApiResDto> checkDuplicateAccount(
            @RequestParam @NotNull String account
    ) {
        authService.checkDuplicateAccount(account);

        return ResponseEntity.status(NO_CONTENT)
                .body(ApiResDto.toSuccessForm(""));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResDto> login(
            @Valid @RequestBody MemberLoginReqDto loginReqDto
    ) {
        AuthResponseDto authResponse = authService.login(loginReqDto);
        return ResponseEntity.status(OK)
                .body(ApiResDto.toSuccessForm(authResponse));
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResDto> logout(
            @RequestHeader("Authorization") String accessToken
            ) {
        String token = resolveToken(accessToken);
        authService.logout(token);
        return ResponseEntity.status(NO_CONTENT)
                .body(ApiResDto.toSuccessForm(""));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResDto> refreshAccessToken(
            @RequestBody RefreshTokenRequestDto refreshTokenRequestDto
    ) {
        AuthResponseDto authResponseDto = authService.refreshAccessToken(refreshTokenRequestDto.getRefreshToken());
        return ResponseEntity.status(OK)
                .body(ApiResDto.toSuccessForm(authResponseDto));
    }

    /**
     * Authorization 헤더에서 "Bearer " 접두사를 제거하여 액세스 토큰을 추출하고 반환
     */
    private String resolveToken(String accessToken) {
        if (accessToken.startsWith("Bearer ")) {
            return accessToken.substring("Bearer ".length());
        }
        return null;
    }


}
