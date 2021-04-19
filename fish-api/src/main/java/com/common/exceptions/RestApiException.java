package com.common.exceptions;

import com.common.api.response.ApiError;

public class RestApiException extends PlatformException {

    private static final long serialVersionUID = 1L;

    private ApiError apiError;

    private Object[] args;

    public RestApiException(ApiError apiError, Object... args) {
        this.apiError = apiError;
        this.args = args;
    }

    public ApiError getApiError() {
        return apiError;
    }

    public Object[] getArgs() {
        return args;
    }

}