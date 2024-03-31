package com.example.cinemaserver.service;

import com.example.cinemaserver.Request.ScheduleRequest;
import com.example.cinemaserver.model.Schedule;
import com.example.cinemaserver.response.ScheduleResponse;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface IScheduleService {
    Schedule getScheduleById(Long scheduleId);

    ScheduleResponse getScheduleResponse(Schedule schedule) throws SQLException;

    List<Schedule> getScheduleByMovieId(Long movieId);

    List<Schedule> getScheduleByRoomId(Long roomId);

    ResponseEntity<String> deleteSchedule(Long id);

    Schedule updateSchedule(Long scheduleId, ScheduleRequest scheduleRequest);

    void addNewSchedule(Long movieId, Long roomId, ScheduleRequest scheduleRequest) throws Exception;

    List<Schedule> getSchedulesByRoomIdDate(Long movieId, Long roomId, LocalDate startDate);

    boolean ordered(Long scheduleId);

    List<Schedule> getSchedulesByBranchIdMovieId(Long branchId,Long movieId);
}
