package com.example.cinemaserver.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {
    private Long id;
    private String startDate;
    private String startTime;
    private String endTime;
    private Double revenue;
    private Long numberOfTickets;
    private Long numberOfSeats;
    private MovieResponse movieResponse;
    private RoomResponse roomResponse;
}
