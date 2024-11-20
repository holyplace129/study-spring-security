package com.security.springsecurity.user.service;

import com.security.springsecurity.auth.infrastructure.JwtUtil;
import com.security.springsecurity.common.redis.dao.RedisDao;
import com.security.springsecurity.user.domain.User;
import com.security.springsecurity.user.domain.repository.UserRepository;
import com.security.springsecurity.user.presentation.dto.request.SignInRequest;
import com.security.springsecurity.user.presentation.dto.request.SignUpRequest;
import com.security.springsecurity.user.presentation.dto.response.SignUpResponse;
import com.security.springsecurity.user.presentation.dto.response.UserDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisDao redisDao;

    @Transactional(readOnly = true)
    public Boolean isValidEmailAndPassword(SignInRequest signInRequest) {
        Optional<User> tempUser = userRepository.findByEmail(signInRequest.getEmail());

        if (tempUser.isPresent()) {
            User user = tempUser.get();

            if (!user.getEmail().equals(signInRequest.getEmail())) {
                return false;
            }

            boolean matches = passwordEncoder.matches(signInRequest.getPassword(), user.getPassword());
            if (!matches) {
                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    public UserDetailResponse signUpUser(SignUpRequest signUpRequest) {
        String encodePassword = passwordEncoder.encode(signUpRequest.getPassword());

        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(encodePassword)
                .build();

        User saveUser = userRepository.save(user);
        return UserDetailResponse.of(saveUser);
    }
}
