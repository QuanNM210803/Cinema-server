package com.example.cinemaserver.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateUserRequest {
    private String fullName;
    private LocalDate dob;
    private MultipartFile photo;
}
