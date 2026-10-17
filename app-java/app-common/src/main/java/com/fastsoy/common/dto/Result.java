package com.fastsoy.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Result<T> {

    /** 业务码，与 Python Code 类保持一致：0000=成功，2xxx=业务错误 */
    private final String code;

    private final String msg;

    private final T data;

    public static <T> Result<T> success(T data) {
        return new Result<T>("0000", "OK", data);
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> fail(String code, String msg) {
        return new Result<T>(code, msg, null);
    }

    public static <T> Result<T> unauthorized(String msg) {
        return fail("2100", msg);
    }

    public static <T> Result<T> credentialsError() {
        return fail("2401", "用户名或密码错误");
    }

    public static <T> Result<T> accountDisabled() {
        return fail("2102", "该用户已被禁用");
    }

    public static <T> Result<T> sessionInvalidated() {
        return fail("2106", "Token已失效，请重新登录");
    }
}
