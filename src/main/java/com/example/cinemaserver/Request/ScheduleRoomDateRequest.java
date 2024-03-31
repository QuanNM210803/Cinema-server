package com.example.cinemaserver.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRoomDateRequest {
    private Long movieId;
    private Long roomId;
    private LocalDate startDate;
}
