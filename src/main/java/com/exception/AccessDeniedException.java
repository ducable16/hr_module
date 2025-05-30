package com.exception;

import com.enums.ErrorCode;

public class AccessDeniedException extends BaseException {
    public AccessDeniedException(String message) {
        super(ErrorCode.ACCESS_DENIED, message);
    }
}
