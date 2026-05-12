package com.tbl324.shared.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private ErrorDetail error;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(ErrorDetail.builder().code(code).message(message).build())
                .build();
    }

    @Getter
    @Builder
    @Jacksonized
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetail {
        private String code;
        private String message;
    }
}
