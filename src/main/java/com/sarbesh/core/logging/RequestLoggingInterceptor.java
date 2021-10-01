package com.sarbesh.core.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RequestLoggingInterceptor extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            if( logger.isInfoEnabled() ) {
                final long end = System.currentTimeMillis();
                logger.info(buildMessage(request, end - start));
            }
        }
    }
    private String buildMessage(HttpServletRequest request, long executionTime) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("|").append(request.getMethod()).append("|");
        buffer.append("|").append(request.getRequestURI()).append("|");
        buffer.append("|ResponseTime=").append(executionTime).append("|");
        return buffer.toString();
    }
}
