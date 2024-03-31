package com.example.cinemaserver.Request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ScheduleRequest {
    private LocalDate startDate;
    private LocalTime startTime;
}
