package com.example.cinemaserver.response;

import com.example.cinemaserver.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private String dob;
    private String avatar;
    private Integer age;
    private Long numberOfTickets;
    private Double totalPayment;
    private Collection<Role> roles;
}
