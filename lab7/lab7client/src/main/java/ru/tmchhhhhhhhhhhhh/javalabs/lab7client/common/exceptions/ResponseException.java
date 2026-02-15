package ru.restaurant.common.exceptions;

public class ResponseException extends RuntimeException {
    public ResponseException(String message) {
        super(message);
    }
    
    public ResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
