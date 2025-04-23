package com.springboot.MyTodoList.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    /**
     * Secret key for signing JWT tokens
     */
    private String secret;

    /**
     * Access token expiration time in milliseconds (default: 1 hour)
     */
    private long expiration = 3600000;

    /**
     * Refresh token expiration time in milliseconds (default: 7 days)
     */
    private long refreshExpiration = 604800000;

    // Getters and setters
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public long getRefreshExpiration() {
        return refreshExpiration;
    }

    public void setRefreshExpiration(long refreshExpiration) {
        this.refreshExpiration = refreshExpiration;
    }
}