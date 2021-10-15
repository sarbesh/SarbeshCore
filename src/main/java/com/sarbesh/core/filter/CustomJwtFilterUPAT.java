package com.sarbesh.core.filter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public interface CustomJwtFilterUPAT {

    UsernamePasswordAuthenticationToken setUPAT(String jwt, String username) throws Exception;
}
