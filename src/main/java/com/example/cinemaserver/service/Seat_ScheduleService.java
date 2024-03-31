package com.example.cinemaserver.service;

import com.example.cinemaserver.model.Schedule;
import com.example.cinemaserver.model.Seat_Schedule;
import com.example.cinemaserver.repository.ScheduleRepository;
import com.example.cinemaserver.repository.Seat_ScheduleRepository;
import com.example.cinemaserver.response.ScheduleResponse;
import com.example.cinemaserver.response.SeatResponse;
import com.example.cinemaserver.response.Seat_ScheduleResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
@AllArgsConstructor
public class Seat_ScheduleService implements ISeat_ScheduleService{
    private final SeatService seatService;
    private final ScheduleService scheduleService;
    private final Seat_ScheduleRepository seatScheduleRepository;
    @Override
    public List<Seat_Schedule> getSeat_ScheduleByScheduleId(Long scheduleId) throws Exception {
        Schedule schedule=scheduleService.getScheduleById(scheduleId);
        if(schedule.getRoom().getStatus()){
            return seatScheduleRepository.findSeat_ScheduleByScheduleId(scheduleId);
        }
        throw new Exception("Can't get a seat because the screening room is not operating.");
    }

    @Override
    public Seat_ScheduleResponse getSeat_ScheduleResponse(Seat_Schedule ss) throws SQLException {
        SeatResponse seatResponse=seatService.getSeatResponse(ss.getSeat());
        ScheduleResponse scheduleResponse=scheduleService.getScheduleResponse(ss.getSchedule());
        return new Seat_ScheduleResponse(ss.getId(),ss.getOrdered(),ss.getPrice(),seatResponse,scheduleResponse);
    }
}
