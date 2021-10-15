package com.sarbesh.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RefreshScope
@ConfigurationProperties(prefix = "security.network.access-control")
public class NetworkAccessProperties {

    private Boolean enable = true;

    private String secret;

    private String header;

    private List<String> ignoreApi = new ArrayList<>();

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public List<String> getIgnoreApi() {
        return ignoreApi;
    }

    public void setIgnoreApi(List<String> ignoreApi) {
        this.ignoreApi = ignoreApi;
    }

    @Override
    public String toString() {
        return "NetworkAccessProperties{" +
                "enable=" + enable +
                ", secret='" + secret + '\'' +
                ", header='" + header + '\'' +
                ", ignoreApi=" + ignoreApi +
                '}';
    }
}
