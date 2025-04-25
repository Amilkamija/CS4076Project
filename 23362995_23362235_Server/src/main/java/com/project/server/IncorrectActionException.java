package com.project.server;

public class IncorrectActionException extends Exception{
    public IncorrectActionException() {
        super("Incorrect Action");
    }

    public IncorrectActionException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
