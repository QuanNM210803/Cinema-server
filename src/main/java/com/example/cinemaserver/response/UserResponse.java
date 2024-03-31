package com.example.cinemaserver.response;

import com.example.cinemaserver.model.Role;
import jakarta.persistence.Lob;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Blob;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private LocalDate dob;
    private String avatar;
    private Integer age;
    private Collection<Role> roles;
}
