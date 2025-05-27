package com.exception;

import com.enums.ErrorCode;

public class EmailExistsException extends BaseException {
    public EmailExistsException() {
        super(ErrorCode.EMAIL_EXISTS, "Email already exists");
    }
}
