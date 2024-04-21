package com.example.cinemaserver.service;

import com.example.cinemaserver.request.ScheduleRequest;
import com.example.cinemaserver.model.Schedule;
import com.example.cinemaserver.response.ScheduleResponse;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface IScheduleService {
    Schedule getScheduleById(Long scheduleId);

    ScheduleResponse getScheduleResponse(Schedule schedule) throws SQLException;

    List<Schedule> getScheduleByMovieId(Long movieId);

    List<Schedule> getScheduleByRoomId(Long roomId);

    void deleteSchedule(Long id);

    Schedule updateSchedule(Long scheduleId, ScheduleRequest scheduleRequest) throws Exception;

    Schedule addNewSchedule(Long movieId, Long roomId, ScheduleRequest scheduleRequest) throws Exception;

    List<Schedule> getSchedulesByRoomIdDate(Long roomId, LocalDate startDate);

    boolean ordered(Long scheduleId);

    List<Schedule> getSchedulesByBranchIdMovieId(Long branchId,Long movieId);

    List<Schedule> getSchedules();
}
