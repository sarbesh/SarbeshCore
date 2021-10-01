package com.sarbesh.core.filter;

import com.sarbesh.core.config.JwtConfig;
import com.sarbesh.core.security.JwtHelper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private JwtConfig jwtConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(jwtConfig.getHEADER_STRING());

        String username = null;
        String jwt = null;

        if(Objects.nonNull(authHeader) && authHeader.startsWith(jwtConfig.getTOKEN_PREFIX())){
            jwt = authHeader.substring(jwtConfig.getTOKEN_PREFIX().length());
            try {
                username = jwtHelper.extractUserName(jwt);
            } catch (IllegalArgumentException e) {
                LOGGER.error("An error occurred while fetching Username from Token, error: {}", e.getMessage());
            } catch (ExpiredJwtException e) {
                LOGGER.warn("The token has expired: {}", e.getMessage());
            } catch(SignatureException e){
                LOGGER.error("Authentication Failed. Username or Password not valid.");
            }
        } else {
            logger.warn("Couldn't find bearer string, header will be ignored");
            filterChain.doFilter(request,response);
            return;
        }

        try{
            if(Objects.nonNull(username) && SecurityContextHolder.getContext().getAuthentication()==null){
                if(jwtHelper.isTokenExpired(jwt)){
                    UsernamePasswordAuthenticationToken upat =
                        new UsernamePasswordAuthenticationToken(username,null,
                                jwtHelper.extractGrantedAuthorities(jwt));
                    LOGGER.info("authenticated user {}, setting security context",username);
                    upat.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(upat);
                }
            }
        } catch (Exception e) {
            // In case of failure. Make sure it's clear; so guarantee user won't be authenticated
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request,response);

    }
}
