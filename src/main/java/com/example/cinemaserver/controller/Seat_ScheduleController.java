package com.example.cinemaserver.controller;

import com.example.cinemaserver.model.Seat_Schedule;
import com.example.cinemaserver.response.Seat_ScheduleResponse;
import com.example.cinemaserver.service.Seat_ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/seat_schedule")
@AllArgsConstructor
public class Seat_ScheduleController {
    private final Seat_ScheduleService seatScheduleService;
    @GetMapping("/get/{scheduleId}")
    public ResponseEntity<?> getSeat_ScheduleByScheduleId(@PathVariable("scheduleId") Long scheduleId){
        try{
            List<Seat_Schedule> seatSchedules= seatScheduleService.getSeat_ScheduleByScheduleId(scheduleId);
            List<Seat_ScheduleResponse> seatScheduleResponses=new ArrayList<>();
            for(Seat_Schedule ss:seatSchedules){
                seatScheduleResponses.add(seatScheduleService.getSeat_ScheduleResponse(ss));
            }
            return ResponseEntity.ok(seatScheduleResponses);
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }
}
