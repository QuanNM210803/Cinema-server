package com.example.cinemaserver.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ScheduleRequest {
    private LocalDate startDate;
    private LocalTime startTime;
}
