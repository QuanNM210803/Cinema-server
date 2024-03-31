package com.example.cinemaserver.Exception;

public class BranchAlreadyExistsException extends RuntimeException {
    public BranchAlreadyExistsException(String message) {
        super(message);
    }
}
