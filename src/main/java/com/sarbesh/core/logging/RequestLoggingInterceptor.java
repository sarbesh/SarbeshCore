package com.sarbesh.core.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingInterceptor extends OncePerRequestFilter {

    @Value("${spring.application.name:logger}")
    private String appName;

    private final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    public RequestLoggingInterceptor() {
        //default constructor
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        StopWatch stopWatch = this.preProcess(request,response);
        filterChain.doFilter(request, response);
        this.postProcess(response,stopWatch);
    }

    private void postProcess(HttpServletResponse response, StopWatch stopWatch) {
        try {
            stopWatch.stop();
            LOGGER.info(MarkerFactory.getMarker("METRICS"), buildResponseMessage(response,stopWatch.getLastTaskTimeMillis()));
        } catch (Exception ex){
            LOGGER.error("Exception {} in postProcess Logging interceptor",ex.getMessage());
        }
    }

    private StopWatch preProcess(HttpServletRequest request, HttpServletResponse response){
        StopWatch stopWatch = null;
        try{
            MDC.clear();
            MDC.put("Method",request.getMethod());
            MDC.put("Uri",request.getRequestURI());
            MDC.put("Query",request.getQueryString());
            MDC.put("User-Agent",request.getHeader("User-Agent"));
            MDC.put("Remote",request.getRemoteHost());
            String uuid = request.getHeader("uuid");
            if(uuid==null){
                uuid = UUID.randomUUID().toString();
                LOGGER.info("Request missing UUID header, created random uuid: {} to track request",uuid);

            }
            MDC.put("UUID",uuid);
            stopWatch = new StopWatch();
            stopWatch.start();
        } catch (Exception ex){
            LOGGER.error("Exception {} in preProcess Logging interceptor",ex.getMessage());
        }
        return stopWatch;
    }
    private String buildResponseMessage(HttpServletResponse response, long executionTime) {
        return "METRICS- |ResponseCode=" + response.getStatus() +
                "|ResponseTime=" + executionTime+"|";
    }
}
