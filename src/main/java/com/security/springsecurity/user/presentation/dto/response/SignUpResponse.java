package com.security.springsecurity.user.presentation.dto.response;

import com.security.springsecurity.user.domain.User;
import lombok.Getter;

@Getter
public class SignUpResponse {

    private final Long id;
    private final String email;
    private final String authority;

    private SignUpResponse(Long id, String email, String authority) {
        this.id = id;
        this.email = email;
        this.authority = authority;
    }

    public static SignUpResponse of(User user) {
        return new SignUpResponse(
                user.getId(),
                user.getEmail(),
                user.getAuthority()
        );
    }
}
