package com.common.api.response;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class ErrorResponse {

    private List<Message> errors;

    private ErrorResponse(List<Message> errors) {
        this.errors = errors;
    }

    public static ErrorResponse of(List<Message> errors) {
        return new ErrorResponse(errors);
    }

    public static ErrorResponse of(ApiError apiError, Object... args) {
        return of(Message.of(apiError.getCode(), apiError.getMessage(), args));
    }

    public static ErrorResponse of(Message msg) {
        return of(Collections.singletonList(msg));
    }

}