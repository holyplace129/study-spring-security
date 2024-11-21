package com.security.springsecurity.oauth.dto.response;

import com.security.springsecurity.user.domain.Provider;
import com.security.springsecurity.user.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfile {

    private String name;
    private String email;
    private String provider;

    public User toEntity() {
        return User.builder()
                .name(this.name)
                .email(this.email)
                .provider(this.provider)
                .build();
    }
}
