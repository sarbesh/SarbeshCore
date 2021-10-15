package com.sarbesh.core.filter;

import com.sarbesh.core.config.NetworkAccessProperties;
import com.sarbesh.core.dto.AccessErrorType;
import com.sarbesh.core.exception.NetworkSecurityException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class NetworkAccessFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkAccessFilter.class);

    @Autowired
    private NetworkAccessProperties networkAccessProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            checkRequest(request);
        } catch (NetworkSecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,"Restricted Network access");
        }
        filterChain.doFilter(request, response);
        responseUpdate(response);
    }

    private void responseUpdate(HttpServletResponse response) {
        if(!networkAccessProperties.getEnable() && response.containsHeader(networkAccessProperties.getHeader())){
            LOGGER.info("NetworkAccessFilter removing header {}",networkAccessProperties.getHeader());
            ((org.apache.catalina.connector.Response)response).getCoyoteResponse()
                    .getMimeHeaders()
                    .removeHeader(networkAccessProperties.getHeader());
        }
    }

    private void checkRequest(HttpServletRequest request) throws NetworkSecurityException {
        if (networkAccessProperties.getEnable()){
            String header = request.getHeader(networkAccessProperties.getHeader());
            if(header==null || StringUtils.isEmpty(header)){
                LOGGER.error("#NetworkAccessFilter Request: {} Tried accessing without Network Security Header",request.getServletPath());
                throw new NetworkSecurityException(AccessErrorType.NETWORK_ACCESS_MISSING);
            }
            if (!header.equals(networkAccessProperties.getSecret())){
                LOGGER.error("#NetworkAccessFilter Request: {} Invalid Network Security Header",request.getServletPath());
                throw new NetworkSecurityException(AccessErrorType.NETWORK_ACCESSS_INVALID);
            }
        }
    }
}
