package com.sarbesh.core.dto;

public enum AccessErrorType {

    NETWORK_ACCESSS_INVALID("Network access token invalid"),
    NETWORK_ACCESS_MISSING("Network access token missing");


    private String value;

    AccessErrorType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
