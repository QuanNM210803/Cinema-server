package com.example.cinemaserver.request;

import lombok.Data;


import java.time.LocalDate;

@Data
public class RegisterUserRequest {
    private String fullName;
    private String email;
    private String password;
    private LocalDate dob;
}
