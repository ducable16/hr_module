package com.exception;

import com.enums.ErrorCode;

public class UsernameExistsException extends BaseException {
    public UsernameExistsException(String username) {
        super(ErrorCode.USERNAME_EXISTS, "Username already exists: " + username);
    }
}
