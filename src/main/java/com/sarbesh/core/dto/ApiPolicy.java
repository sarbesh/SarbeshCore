package com.sarbesh.core.dto;

import org.springframework.http.HttpMethod;

import java.util.List;

public class ApiPolicy {
    private List<String> url;
    private List<HttpMethod> httpMethod;

    public ApiPolicy() {
    }

    public ApiPolicy(List<String> url, List<HttpMethod> httpMethod) {
        this.url = url;
        this.httpMethod = httpMethod;
    }

    @Override
    public String toString() {
        return "url='" + url + '\'' + ", httpMethod=" + httpMethod;
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }

    public List<HttpMethod> getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(List<HttpMethod> httpMethod) {
        this.httpMethod = httpMethod;
    }
}
