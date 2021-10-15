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

@Component
//@Order(Ordered.HIGHEST_PRECEDENCE+1)
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Value("${security.service-auth.disable:false}")
    private boolean isNotEnabled;

    @Autowired(required = false)
    CustomJwtFilterUPAT customJwtFilterUPAT;

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

        logger.debug("#JwtRequestFilter starting jwt filter ");
        if(Objects.nonNull(authHeader) && authHeader.startsWith(jwtConfig.getTOKEN_PREFIX())){
            jwt = authHeader.substring(jwtConfig.getTOKEN_PREFIX().length());
            try {
                username = jwtHelper.extractUserName(jwt);
            } catch (IllegalArgumentException e) {
                LOGGER.error("#JwtRequestFilter An error occurred while fetching Username from Token, error: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Token invalid");
                return;
            } catch (ExpiredJwtException e) {
                LOGGER.warn("#JwtRequestFilter The token has expired: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Token expired");
                return;
            } catch(SignatureException e){
                LOGGER.error("#JwtRequestFilter Authentication Failed. Username or Password not valid.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Token signature invalid");
                return;
            }
        } else {
            logger.warn("#JwtRequestFilter Couldn't find bearer string, header will be ignored");
            filterChain.doFilter(request,response);
            return;
        }

        try{
            if(Objects.nonNull(username) && SecurityContextHolder.getContext().getAuthentication()==null){
                if(!jwtHelper.isTokenExpired(jwt)){
                    UsernamePasswordAuthenticationToken upat;
                    if(customJwtFilterUPAT!=null){
                        upat = customJwtFilterUPAT.setUPAT(jwt,username);
                    } else {
                        upat = new UsernamePasswordAuthenticationToken(username,null,
                                jwtHelper.extractGrantedAuthorities(jwt));
                    }
                    LOGGER.info("#JwtRequestFilter authenticated user {}, setting security context",username);
                    upat.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    LOGGER.debug("#JwtRequestFilter Setting upat for {} with authorities: {}",upat.getPrincipal(), upat.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(upat);
                } else {
                    LOGGER.error("#JwtRequestFilter Authentication expired for {}",username);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Authentication expired");
                }
            }
        } catch (Exception e) {
            // In case of failure. Make sure it's clear; so guarantee user won't be authenticated
            LOGGER.debug("#JwtRequestFilter Clearing security context due to exception {} ",e.getMessage());
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"unable to process auth token");
        }

        filterChain.doFilter(request,response);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return this.isNotEnabled;
    }
}
