package com.security.springsecurity.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.security.springsecurity.auth.dto.response.TokenResponse;
import com.security.springsecurity.auth.infrastructure.JwtUtil;
import com.security.springsecurity.oauth.dto.response.OAuthAttributes;
import com.security.springsecurity.oauth.dto.response.UserProfile;
import com.security.springsecurity.user.domain.User;
import com.security.springsecurity.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        // OAuth2 서비스 구분(Google, Naver, Kakao)
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // 로그인을 수행한 서비스의 이름


        String userNameAttributeName = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName(); // PK가 되는 정보

        // 유저 정보
        Map<String, Object> attributes = oAuth2User.getAttributes(); // // 사용자가 가지고 있는 정보

        // 서비스별 사용자 프로필 추출
        UserProfile userProfile = OAuthAttributes.extract(registrationId, attributes);
        userProfile.setProvider(registrationId);

        // 사용자 정보 저장 또는 업데이트
        User user = updateOrSaveUser(userProfile);

        // JWT 토큰 생성
        TokenResponse tokenResponse;

        try {
            tokenResponse = jwtUtil.createTokensBySignIn(user.getEmail());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("토큰 생성 오류 발생", e);
        }

        // 사용자 정보, 토큰 반환
        Map<String, Object> customAttribute =
                getCustomAttribute(registrationId, userNameAttributeName, attributes, userProfile);

        customAttribute.put("accessToken", tokenResponse.getAtk());
        customAttribute.put("refreshToken", tokenResponse.getRtk());

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")),
                customAttribute,
                userNameAttributeName
        );
    }

    public Map getCustomAttribute(String registrationId,
                                  String nameAttributeName,
                                  Map<String, Object> attributes,
                                  UserProfile userProfile) {
        Map<String, Object> customAttribute = new ConcurrentHashMap<>();

        customAttribute.put(nameAttributeName, attributes.get(nameAttributeName));
        customAttribute.put("provider", registrationId);
        customAttribute.put("name", userProfile.getName());
        customAttribute.put("email", userProfile.getEmail());

        return customAttribute;
    }

    private User updateOrSaveUser(UserProfile userProfile) {
        User user = userRepository
                .findByEmailAndProvider(userProfile.getEmail(), userProfile.getProvider())
                .map(value -> value.updateUser(userProfile.getName(), userProfile.getEmail()))
                .orElse(userProfile.toEntity());

        return userRepository.save(user);
    }
}
