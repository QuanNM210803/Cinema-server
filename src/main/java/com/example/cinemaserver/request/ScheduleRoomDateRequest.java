package com.example.cinemaserver.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
//phuc vu cho viec them lich chieu
public class ScheduleRoomDateRequest {
    private Long movieId;
    private Long roomId;
    private LocalDate startDate;
}
