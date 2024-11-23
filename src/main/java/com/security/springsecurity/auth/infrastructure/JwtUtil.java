package com.security.springsecurity.auth.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.security.springsecurity.auth.dto.Subject;
import com.security.springsecurity.auth.dto.response.TokenResponse;
import com.security.springsecurity.common.redis.dao.RedisDao;
import com.security.springsecurity.user.domain.User;
import com.security.springsecurity.user.domain.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;

@Component
public class JwtUtil {

    private final ObjectMapper objectMapper;
    private final RedisDao redisDao;
    private final UserRepository userRepository;
    private final Long accessTokenExpirationTime;
    private final Long refreshTokenExpirationTime;
    private final SecretKey SECRET_KEY;

    public JwtUtil(
            ObjectMapper objectMapper,
            RedisDao redisDao,
            UserRepository userRepository,
            @Value("${spring.jwt.key}") final String key,
            @Value("${spring.jwt.live.atk}") final Long accessTokenExpirationTime,
            @Value("${spring.jwt.live.rtk}") final Long refreshTokenExpirationTime
    ) {
        this.objectMapper = objectMapper;
        this.redisDao = redisDao;
        this.userRepository = userRepository;
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
        this.SECRET_KEY = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    public TokenResponse createTokensBySignIn(String email) throws JsonProcessingException {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return createTokenResponse(user);
    }

    private TokenResponse createTokenResponse(User user) throws JsonProcessingException {
        Subject atkSubject = Subject.atk(
                user.getId(),
                user.getEmail(),
                user.getAuthority()
        );
        Subject rtkSubject = Subject.rtk(
                user.getId(),
                user.getEmail(),
                user.getAuthority()
        );

        String atk = createToken(atkSubject, accessTokenExpirationTime);
        String rtk = createToken(rtkSubject, refreshTokenExpirationTime);

        redisDao.setValues(user.getEmail(), rtk, Duration.ofMillis(refreshTokenExpirationTime));

        return TokenResponse.builder()
                .atk(atk)
                .rtk(rtk)
                .atkExpiration(getTokenExpiration(atk))
                .rtkExpiration(getTokenExpiration(rtk))
                .build();
    }

    // 토큰 생성 로직
    private String createToken(Subject subject, Long tokenLive) throws JsonProcessingException {
        String subjectStr = objectMapper.writeValueAsString(subject);
        Claims claims = Jwts.claims()
                .setSubject(subjectStr);
        Date date = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + tokenLive))
                .signWith(SECRET_KEY)
                .compact();
    }

    public TokenResponse renewToken(String rtk) throws JsonProcessingException {
        Subject subject = getSubject(rtk);

        String rtkInRedis = redisDao.getValues(subject.getEmail());

        if (Objects.isNull(rtkInRedis) || !subject.getType().equals("RTK"))
            throw new BadCredentialsException("만료된 RefreshToken입니다.");

        redisDao.deleteValues(subject.getEmail()); // 갱신을 위해 RefreshToken 제거

        User user = userRepository.findByEmail(subject.getEmail()).orElseThrow(() -> new IllegalArgumentException("잘못된 이용자 입니다."));

        return createTokenResponse(user);
    }

    /* 토큰에 담긴 유저 정보(Subject)를 추출하는 함수 */
    public Subject getSubject(String atk) throws JsonProcessingException {
        String subjectStr = parseToken(atk)
                .getBody()
                .getSubject();
        return objectMapper.readValue(subjectStr, Subject.class);
    }

    public Date getTokenExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody().getExpiration();
    }

    public Jws<Claims> parseToken(final String atk) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(atk);
    }

    /* 추후 예외 처리를 위해 추가하였습니다 */
    public void validateAccessToken(final String atk) {
        try {
            parseToken(atk);
        } catch (final ExpiredJwtException e ) {
            System.out.println(new BadCredentialsException("만료된 토큰입니다.").getMessage());
        } catch (final JwtException | IllegalArgumentException e) {
            System.out.println(new BadCredentialsException("잘못된 토큰입니다.").getMessage());
        }
    }

    /* 추후 예외 처리를 위해 추가하였습니다 */
    public void validateRefreshToken(final String rtk) {
        try {
            parseToken(rtk);
        } catch (final ExpiredJwtException e ) {
            throw new BadCredentialsException("만료된 토큰입니다.");
        } catch (final JwtException | IllegalArgumentException e) {
            throw new BadCredentialsException("잘못된 토큰입니다.");
        }
    }
}