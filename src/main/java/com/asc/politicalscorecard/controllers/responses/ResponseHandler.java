package com.asc.politicalscorecard.controllers.responses;

public class ResponseHandler {

    public static <T> ApiResponse<T> generateSuccessResponse(T data) {
        return new ApiResponse<>(true, "Operation successful", data);
    }

    public static <T> ApiResponse<T> generateSuccessResponse(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static ApiResponse<Void> generateSuccessResponse(String message) {
        return new ApiResponse<>(true, message, null);
    }
}