package com.sarbesh.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
public class JwtConfig {

    @Value("${jwt.signingKey:mySignKey}")
    private String SIGNING_KEY;

    @Value("${jwt.token.validity:36000}")
    private long TOKEN_VALIDITY;

    @Value("${jwt.authorities.key:roles}")
    public String AUTHORITIES_KEY;

    @Value("${jwt.header.string:Authorization}")
    public String HEADER_STRING;

    @Value("${jwt.token.prefix:Bearer }")
    public String TOKEN_PREFIX;

    public String getSIGNING_KEY() {
        return SIGNING_KEY;
    }

    public long getTOKEN_VALIDITY() {
        return TOKEN_VALIDITY;
    }

    public String getAUTHORITIES_KEY() {
        return AUTHORITIES_KEY;
    }

    public String getHEADER_STRING() {
        return HEADER_STRING;
    }

    public String getTOKEN_PREFIX() {
        return TOKEN_PREFIX;
    }
}
