package com.security.springsecurity.auth.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.security.springsecurity.auth.dto.response.AccessTokenResponse;
import com.security.springsecurity.auth.dto.response.ResultResponse;
import com.security.springsecurity.auth.dto.response.TokenResponse;
import com.security.springsecurity.auth.infrastructure.JwtUtil;
import com.security.springsecurity.user.presentation.dto.request.SignInRequest;
import com.security.springsecurity.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
public class LoginApi {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    private Cookie createRefreshTokenCookie(TokenResponse tokenResponse) {
        Cookie cookie = new Cookie("rtk", tokenResponse.getRtk());
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        Date now = new Date();
        int age = (int) (tokenResponse.getRtkExpiration().getTime() - now.getTime()) / 1000;
        System.out.println("cookie = " + cookie);
        return cookie;
    }

    @PostMapping("/auth/signin")
    public ResultResponse<AccessTokenResponse> signIn(@RequestBody SignInRequest signInRequest, HttpServletResponse httpServletResponse) throws JsonProcessingException {
        System.out.println("signInRequest = " + signInRequest);

        if (userService.isValidEmailAndPassword(signInRequest)) {
            TokenResponse tokenResponse = jwtUtil.createTokensBySignIn(signInRequest.getEmail());
            System.out.println("tokenResponse = " + tokenResponse);

            Cookie cookie = createRefreshTokenCookie(tokenResponse);
            System.out.println("cookie = " + cookie);

            httpServletResponse.addCookie(cookie);

            return new ResultResponse<>(HttpStatus.OK.value(), "success", AccessTokenResponse.builder()
                    .atk(tokenResponse.getAtk())
                    .atkExpiration(tokenResponse.getAtkExpiration())
                    .build());
        } else {
            return new ResultResponse<>(HttpStatus.BAD_REQUEST.value(), "fail", null);
        }

    }
}
