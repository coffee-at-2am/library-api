package com.transistorwebservices.libraryapi.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transistorwebservices.libraryapi.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        User user = null;

        try {
            user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),
                    user.getPassword(), new ArrayList<>()));
        } catch (BadCredentialsException e) {
            logger.error("Authentication failed for username: {}", user.getUsername(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while authenticating the username: {} ", user.getUsername(), e.getMessage());
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication auth)
            throws IOException, ServletException {
        User user = (User) auth.getPrincipal();

        String token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .withClaim("userId", user.getUserId())
                .withClaim("role", user.getRole().toString())
                .sign(HMAC512(SecurityConstants.SIGNING_SECRET.getBytes()));

        response.addHeader(SecurityConstants.AUTHORIZATION_HEADER,
                SecurityConstants.BEARER_TOKEN_PREFIX + token);
    }
}
