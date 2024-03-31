package com.example.cinemaserver.service;

import com.example.cinemaserver.model.Seat_Schedule;
import com.example.cinemaserver.response.Seat_ScheduleResponse;

import java.sql.SQLException;
import java.util.List;

public interface ISeat_ScheduleService {

    List<Seat_Schedule> getSeat_ScheduleByScheduleId(Long scheduleId) throws Exception;

    Seat_ScheduleResponse getSeat_ScheduleResponse(Seat_Schedule ss) throws SQLException;
}
