package com.example.cinemaserver.Request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BillRequest {
    private LocalDateTime createdTime;
    private Long userId;
}
