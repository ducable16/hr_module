package com.exception;


import com.enums.ErrorCode;

public class EntityNotFoundException extends BaseException {
    public EntityNotFoundException(String message) {
        super(ErrorCode.ENTITY_NOT_FOUND, message);
    }
}
