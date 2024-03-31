package com.example.cinemaserver.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat_ScheduleResponse {
    private Long id;
    private Boolean ordered;
    private Double price;
    private SeatResponse seatResponse;
    private ScheduleResponse scheduleResponse;
}
