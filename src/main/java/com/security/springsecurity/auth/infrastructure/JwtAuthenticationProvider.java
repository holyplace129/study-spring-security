package com.security.springsecurity.auth.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.security.springsecurity.auth.dto.Subject;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationProvider(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (!supports(authentication.getClass())) {
            return null;
        }

        String atk = (String) authentication.getPrincipal();

        /* JwtToken 검증, 실패 시 예외 발생 */

        if (atk != null) {
            jwtUtil.validateAccessToken(atk);
            try {
                Subject subject = jwtUtil.getSubject(atk);

                String email = subject.getEmail();
                String authorities = subject.getAuthority();
                String employeeName = subject.getName();

                return new UsernamePasswordAuthenticationToken(
                        email,
                        employeeName,
                        AuthorityUtils
                                .commaSeparatedStringToAuthorityList(authorities)
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            Authentication failToken =  new UsernamePasswordAuthenticationToken(null, null, null);
            failToken.setAuthenticated(false);
            return failToken;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}