package com.security.springsecurity.user.presentation.dto.response;

import com.security.springsecurity.user.domain.Provider;
import com.security.springsecurity.user.domain.User;
import lombok.Getter;

@Getter
public class SignUpResponse {

    private final Long id;
    private final String email;
    private final String authority;
    private final String provider;

    private SignUpResponse(Long id, String email, String authority, String provider) {
        this.id = id;
        this.email = email;
        this.authority = authority;
        this.provider = provider;
    }

    public static SignUpResponse of(User user) {
        return new SignUpResponse(
                user.getId(),
                user.getEmail(),
                user.getAuthority(),
                user.getProvider()
        );
    }
}
