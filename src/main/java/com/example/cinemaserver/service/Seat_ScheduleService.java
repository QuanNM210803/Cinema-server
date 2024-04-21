package com.example.cinemaserver.service;

import com.example.cinemaserver.model.Seat_Schedule;
import com.example.cinemaserver.repository.Seat_ScheduleRepository;
import com.example.cinemaserver.response.ScheduleResponse;
import com.example.cinemaserver.response.SeatResponse;
import com.example.cinemaserver.response.Seat_ScheduleResponse;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class Seat_ScheduleService implements ISeat_ScheduleService{
    private final SeatService seatService;
    private final ScheduleService scheduleService;
    private final Seat_ScheduleRepository seatScheduleRepository;
    @Override
    public List<Seat_Schedule> getSeat_ScheduleByScheduleId(Long scheduleId) {
        scheduleService.getScheduleById(scheduleId);
        return seatScheduleRepository.findSeat_ScheduleByScheduleId(scheduleId);
    }

    @Override
    public Seat_ScheduleResponse getSeat_ScheduleResponse(Seat_Schedule ss) {
        SeatResponse seatResponse=seatService.getSeatResponse(ss.getSeat());
        ScheduleResponse scheduleResponse=scheduleService.getScheduleResponse(ss.getSchedule());
        return new Seat_ScheduleResponse(ss.getId(),ss.getOrdered(),ss.getPrice(),seatResponse,scheduleResponse);
    }

    // xoá seat_schedule của lịch chiếu đã qua
    @Scheduled(cron = "0 0 0 * * *")
    public void autoDeleteSeat_schedule(){
        List<Seat_Schedule> seatSchedules=seatScheduleRepository.findSeat_SchedulePast(LocalDate.now().minusDays(3));
        seatSchedules.forEach(seatSchedule -> seatScheduleRepository.deleteById(seatSchedule.getId()));
    }
}
