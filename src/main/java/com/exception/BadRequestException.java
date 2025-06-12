package com.exception;

import com.enums.ErrorCode;

public class BadRequestException extends BaseException {

    public BadRequestException(String message) {
        super(ErrorCode.INVALID_INPUT, message);
    }
}
