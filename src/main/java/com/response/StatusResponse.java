package com.response;

import lombok.Getter;

@Getter
public class StatusResponse {
    String message;
    public StatusResponse(String message) {
        this.message = message;
    }
}
