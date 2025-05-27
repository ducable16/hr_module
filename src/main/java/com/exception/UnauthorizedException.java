package com.exception;

import com.enums.ErrorCode;

public class UnauthorizedException extends BaseException {

    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED, "Not authorized");
    }
}
