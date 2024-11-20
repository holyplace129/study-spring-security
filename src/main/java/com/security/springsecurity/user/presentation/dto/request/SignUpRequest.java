package com.security.springsecurity.user.presentation.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class SignUpRequest {

    private String email;
    private String password;

    @Builder
    public SignUpRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
