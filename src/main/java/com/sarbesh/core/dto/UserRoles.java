package com.sarbesh.core.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRoles {
    VISITOR("VISITOR"),
    USER("USER"),
    ADMIN("ADMIN");

    private String value;

    UserRoles(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    public static UserRoles fromValue(String text){
        for(UserRoles b : UserRoles.values()){
            if(String.valueOf(b).equalsIgnoreCase(text)){
                return b;
            }
        }
        return null;
    }
}
