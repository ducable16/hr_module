package com.enums;

public enum ErrorCode {
    ENTITY_NOT_FOUND(404),
    INVALID_INPUT(400),
    BAD_REQUEST(400),
    USERNAME_EXISTS(409),
    EMAIL_EXISTS(409),
    UNAUTHORIZED(401),
    WRONG_PASSWORD(401),
    INVALID_OTP(401),
    ACCESS_DENIED(403),
    DUPLICATE_RESOURCE(409),
    INTERNAL_ERROR(500);

    private final int httpStatus;

    ErrorCode(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
