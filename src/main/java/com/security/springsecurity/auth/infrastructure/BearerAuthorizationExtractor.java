package com.security.springsecurity.auth.infrastructure;

import org.springframework.stereotype.Component;

@Component
public class BearerAuthorizationExtractor {

    private static final String AUTHORIZATION_TYPE = "Bearer ";

    public String extractAccesstoken(String header) {
        if (header != null && header.startsWith(AUTHORIZATION_TYPE)) {
            return header.substring(AUTHORIZATION_TYPE.length()).trim();
        }
        else return null;
        /* 예외 처리하는 순간 토큰이 필요없는 요청도 막게된다 . */
//        throw new IllegalArgumentException("유효하지 않은 Access Token 입니다.");
    }

}
