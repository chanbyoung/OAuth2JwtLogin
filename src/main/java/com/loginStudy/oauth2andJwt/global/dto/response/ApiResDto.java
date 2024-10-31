package com.loginStudy.oauth2andJwt.global.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResDto {
    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_FAIL = "fail";
    private static final String STATUS_ERROR = "error";

    // 상태 (성공,실패,에러)
    private String status;
    // 실패/에러 메시지 (성공일시 null)
    private String message;
    // 데이터 (실패/에러일 때 null)
    private Object data;

    /**
     * 성공했을 때, 반환하는 APIResponse
     *
     * @param data 데이터 내용
     * @return 성공 APIResponse
     */
    public static ApiResDto toSuccessForm(Object data) {
        return new ApiResDto(STATUS_SUCCESS, null, data);
    }

    /**
     * 실패했을 때, 반환하는 APIResponse
     *
     * @param message 실패 메시지
     * @return 실패 APIResponse
     */
    public static ApiResDto toFailForm(String message) {
        return new ApiResDto(STATUS_FAIL, message, null);
    }

    /**
     * 예외처리가 됐을 때, 반환하는 APIResponse
     *
     * @param message 에러 메시지
     * @return 에러 APIResponse
     */
    public static ApiResDto toErrorForm(String message) {
        return new ApiResDto(STATUS_ERROR, message, null);
    }
}
