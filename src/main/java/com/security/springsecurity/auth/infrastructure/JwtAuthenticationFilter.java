package com.security.springsecurity.auth.infrastructure;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final BearerAuthorizationExtractor extractor;

    public JwtAuthenticationFilter(String processUrl,
                                   BearerAuthorizationExtractor extractor,
                                   AuthenticationManager authenticationManager) {
        super(processUrl);
        this.extractor = extractor;
        setAuthenticationManager(authenticationManager);
        setAuthenticationSuccessHandler(successHandler());
    }

    private AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String atk = extractor.extractAccesstoken(request.getHeader(HttpHeaders.AUTHORIZATION));
        Authentication authentication = new JwtAuthenticationToken(atk);
        return getAuthenticationManager().authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws ServletException, IOException {

        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }
}