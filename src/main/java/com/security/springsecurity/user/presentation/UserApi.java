package com.security.springsecurity.user.presentation;

import com.security.springsecurity.user.presentation.dto.request.SignUpRequest;
import com.security.springsecurity.user.presentation.dto.response.SignUpResponse;
import com.security.springsecurity.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserApi {

    private final UserService userService;

    @PostMapping("auth/signup")
    public ResponseEntity<SignUpResponse> signUpUser(@RequestBody SignUpRequest signUpRequest) {
        System.out.println("signUpRequest = " + signUpRequest);

        SignUpResponse response = userService.signUpUser(signUpRequest);
        return ResponseEntity.ok(response);
    }
}
