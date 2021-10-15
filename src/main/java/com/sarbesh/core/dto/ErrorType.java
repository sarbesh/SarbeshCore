package com.sarbesh.core.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorType {
    WARN("WARN"),
    FATAL("FATAL"),
    ERROR("ERROR");

    private String value;

    ErrorType(String value) {this.value = value;}

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    public static ErrorType fromValue(String text){
        for(ErrorType b : ErrorType.values()){
            if(String.valueOf(b).equalsIgnoreCase(text)){
                return b;
            }
        }
        return null;
    }
}
