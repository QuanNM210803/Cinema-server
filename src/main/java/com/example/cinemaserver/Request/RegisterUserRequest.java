package com.example.cinemaserver.Request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDate;

@Data
public class RegisterUserRequest {
    private String fullName;
    private String email;
    private String password;
    private LocalDate dob;
}
