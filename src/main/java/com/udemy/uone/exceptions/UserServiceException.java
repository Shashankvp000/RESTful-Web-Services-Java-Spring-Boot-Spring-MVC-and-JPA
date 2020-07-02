package com.udemy.uone.exceptions;

public class UserServiceException extends RuntimeException {

    private static final long serialVersionUID = 6504667412919848339L;

    public UserServiceException(String message) {
        super(message);
    }

}
