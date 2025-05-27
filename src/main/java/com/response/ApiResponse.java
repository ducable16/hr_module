package com.response;

import com.enums.ErrorCode;
import lombok.Data;

@Data
public class ApiResponse<T> {
    private int status;
    private boolean success;
    private String message;
    private T data;
    private String errorCode;

    public ApiResponse(ErrorCode errorCode, boolean success, String message, T data) {
        this.status = errorCode.getHttpStatus();
        this.success = success;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode.name();
    }

    public ApiResponse(int status, boolean success, String message, T data, String errorCode) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
    }
    public static <T> ApiResponse<T> successWithMessage(T data) {
        return new ApiResponse<>(200, true, data.toString(), null, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, true, "OK", data, null);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String message) {
        return new ApiResponse<>(errorCode, false, message, null);
    }

}
