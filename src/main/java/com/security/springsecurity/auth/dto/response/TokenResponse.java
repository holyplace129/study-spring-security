package com.security.springsecurity.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
public class TokenResponse {

    private String atk;
    private String rtk;
    private Date atkExpiration;
    private Date rtkExpiration;


    @Builder
    public TokenResponse(String atk, String rtk, Date atkExpiration, Date rtkExpiration) {
       this.atk = atk;
       this.rtk = rtk;
       this.atkExpiration = atkExpiration;
       this.rtkExpiration = rtkExpiration;
    }
}
