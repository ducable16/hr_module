package com.exception;

import com.enums.ErrorCode;

public class BadRequestException extends BaseException {

    public BadRequestException() {
        super(ErrorCode.INVALID_INPUT, "Bad request");
    }
}
