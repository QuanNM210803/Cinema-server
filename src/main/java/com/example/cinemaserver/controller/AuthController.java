package com.example.cinemaserver.controller;

import com.example.cinemaserver.Exception.UserAlreadyExistsException;
import com.example.cinemaserver.Request.LoginRequest;
import com.example.cinemaserver.Request.UserRequest;
import com.example.cinemaserver.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/registerUser")
    public ResponseEntity<String> addNewUser(@ModelAttribute UserRequest userRequest){
        try{
            userService.registerUser(userRequest);
            return ResponseEntity.ok("Register successfully.");
        }catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){

        return ResponseEntity.ok("successfully");
    }
}
