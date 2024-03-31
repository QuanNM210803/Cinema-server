package com.example.cinemaserver.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillResponse {
    private Long id;
    private LocalDateTime createdTime;
    private UserResponse userResponse;
}
