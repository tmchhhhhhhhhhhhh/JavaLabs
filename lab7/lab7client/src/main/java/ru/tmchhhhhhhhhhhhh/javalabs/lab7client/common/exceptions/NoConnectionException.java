package ru.tmchhhhhhhhhhhhh.javalabs.lab7client.common.exceptions;

public class NoConnectionException extends RuntimeException {
    public NoConnectionException(String message) {
        super(message);
    }
    
    public NoConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
