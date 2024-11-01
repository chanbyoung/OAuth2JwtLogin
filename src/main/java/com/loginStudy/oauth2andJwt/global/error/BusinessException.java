package com.loginStudy.oauth2andJwt.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    //오류 발생 부분의 값. 명확하게 없으면 Null.
    private final String invalidValue;
    //오류 필드명.
    private final String fieldName;
    //오류 상태 코드.
    private final HttpStatus httpStatus;
    //오류 메시지
    private final String message;

    /**
     * 비지니스 에러를 ErroCode Enum으로 생성
     *
     * @param invalidValue 오류 발생 부분의 값
     * @param fieldName    오류 필드 명
     * @param errorCode    오류 상태코드와 메시지가 담긴 Enum
     */
    public BusinessException(Object invalidValue, String fieldName, ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.invalidValue = invalidValue != null ? invalidValue.toString() : null;
        this.fieldName = fieldName;
        this.httpStatus = errorCode.getHttpStatus();
        this.message = errorCode.getMessage();
    }
}
