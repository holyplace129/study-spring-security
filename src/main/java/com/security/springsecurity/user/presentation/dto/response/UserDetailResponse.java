package com.security.springsecurity.user.presentation.dto.response;

import com.security.springsecurity.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserDetailResponse {

    private final Long id;
    private final String email;
    private final String authority;
    private final Boolean isDeleted;

    @Builder
    public UserDetailResponse(Long id, String email, String authority, Boolean isDeleted) {
        this.id = id;
        this.email = email;
        this.authority = authority;
        this.isDeleted = isDeleted;
    }

    public static UserDetailResponse of(User user) {
        return new UserDetailResponse(
                user.getId(),
                user.getEmail(),
                user.getAuthority(),
                user.getIsDeleted()
        );
    }
}
