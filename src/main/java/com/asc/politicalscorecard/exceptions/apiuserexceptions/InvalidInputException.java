package com.asc.politicalscorecard.exceptions.apiuserexceptions;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}