package com.security.springsecurity.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
public class AccessTokenResponse {

    private String atk;
    private Date atkExpiration;

    @Builder
    public AccessTokenResponse(String atk, Date atkExpiration) {
        this.atk = atk;
        this.atkExpiration = atkExpiration;
    }
}
