package com.sarbesh.core.dto;

import java.util.List;

public class AccessPolicy {

    private List<ApiPolicy> api;
    private List<UserRoles> allowedRoles;

    public List<ApiPolicy> getApi() {
        return api;
    }

    public void setApi(List<ApiPolicy> api) {
        this.api = api;
    }

    public List<UserRoles> getAllowedRoles() {
        return allowedRoles;
    }

    public void setAllowedRoles(List<UserRoles> allowedRoles) {
        this.allowedRoles = allowedRoles;
    }

    @Override
    public String toString() {
        return "api=" + api +
                ", allowedRoles=" + allowedRoles;
    }

    public AccessPolicy() {
    }

    public AccessPolicy(List<ApiPolicy> api, List<UserRoles> allowedRoles) {
        this.api = api;
        this.allowedRoles = allowedRoles;
    }
}
