package com.transistorwebservices.libraryapi.security;

public class SecurityConstants {

    public static final String SIGNING_SECRET = "MyApiSecret";
    public static final String BEARER_TOKEN_PREFIX = "Bearer";
    public static final long EXPIRATION_TIME = 1800000;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String NEW_USER_REGISTRATION_URL = "/v1/users";
    public static final String NEW_USER_DEFAULT_PASSWORD = "Password123";
}
