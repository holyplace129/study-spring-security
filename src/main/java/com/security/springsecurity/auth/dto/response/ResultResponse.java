package com.security.springsecurity.auth.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResultResponse<T> {

    private Integer code;
    private String message;
    private T date;

    public ResultResponse(Integer code, String message, T date) {
        this.code = code;
        this.message = message;
        this.date = date;
    }
}
