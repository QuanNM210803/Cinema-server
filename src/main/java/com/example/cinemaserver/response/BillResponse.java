package com.example.cinemaserver.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillResponse {
    private Long id;
    private String createdDate;
    private String createdTime;
    private Double payment;
    private Long numberOfTickets;
    private UserResponse userResponse;
}
