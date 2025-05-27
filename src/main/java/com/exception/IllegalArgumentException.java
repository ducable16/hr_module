package com.exception;

import com.enums.ErrorCode;

public class IllegalArgumentException extends BaseException {

    public IllegalArgumentException(String message) {
        super(ErrorCode.INTERNAL_ERROR, message);
    }
}
