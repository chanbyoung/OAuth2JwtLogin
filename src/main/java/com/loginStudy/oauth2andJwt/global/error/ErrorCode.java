package com.loginStudy.oauth2andJwt.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    MEMBER_WRONG_PASSWORD_CONFIRM("비밀번호가 서로 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    MEMBER_ACCOUNT_DUPLICATE("중복된 아이디 입니다.", HttpStatus.BAD_REQUEST),
    MEMBER_ACCOUNT_NOT_FOUND("가입되지 않은 아이디 입니다.", HttpStatus.NOT_FOUND);


    //오류 메시지
    private final String message;
    private final HttpStatus httpStatus;
}
