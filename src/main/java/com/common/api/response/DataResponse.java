package com.common.api.response;

import lombok.Data;

@Data
public class DataResponse<T> {

    @SuppressWarnings("rawtypes")
    private static final DataResponse FAILURE = new DataResponse<>(false);

    @SuppressWarnings("rawtypes")
    private static final DataResponse SUCCESS = new DataResponse<>(true);

    private boolean success;

    private T content;

    private DataResponse(T content) {
        this.content = content;
        success = true;
    }

    private DataResponse(boolean success) {
        this.success = success;
    }

    public static final <T> DataResponse<T> of(T content) {
        return new DataResponse<T>(content);
    }

    @SuppressWarnings("unchecked")
    public static final <T> DataResponse<T> of(boolean success) {
        return (success ? SUCCESS : FAILURE);
    }

    @SuppressWarnings("unchecked")
    public static final <T> DataResponse<T> fail() {
        return (DataResponse<T>) FAILURE;
    }

    @SuppressWarnings("unchecked")
    public static final <T> DataResponse<T> success() {
        return (DataResponse<T>) SUCCESS;
    }

}