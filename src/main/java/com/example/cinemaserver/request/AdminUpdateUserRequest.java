package com.example.cinemaserver.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
public class AdminUpdateUserRequest {
    private String fullName;
    private String email;
    private List<Long> rolesId;
    private LocalDate dob;
    private MultipartFile photo;
}
