package com.sarbesh.core.dto;

public class ErrorResponse {
    private ErrorType errorType;
    private String errorCode;
    private String errorDetail;
    private String errorMessage;

    public ErrorResponse() {
    }

    public ErrorResponse(ErrorType errorType, String errorCode, String errorDetail, String errorMessage) {
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.errorDetail = errorDetail;
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "errorType=" + errorType +
                ", errorCode='" + errorCode + '\'' +
                ", errorDetail='" + errorDetail + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
