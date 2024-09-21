package com.asc.politicalscorecard.controllers.responses;

public class CreateSuccessResponse {
    private String message;

    public CreateSuccessResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}