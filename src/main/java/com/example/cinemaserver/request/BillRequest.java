package com.example.cinemaserver.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BillRequest {
    private LocalDateTime createdTime;
    private Long userId;
}
