package com.example.cinemaserver.Exception;

public class RoleAlreadyExistsException extends RuntimeException{
    public RoleAlreadyExistsException(String message){
        super(message);
    }
}
