package com.example.cinemaserver.controller;

import com.example.cinemaserver.Request.ScheduleRequest;
import com.example.cinemaserver.Request.ScheduleRoomDateRequest;
import com.example.cinemaserver.model.Room;
import com.example.cinemaserver.model.Schedule;
import com.example.cinemaserver.response.ScheduleResponse;
import com.example.cinemaserver.service.RoomService;
import com.example.cinemaserver.service.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/schedules")
@AllArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final RoomService roomService;
    @GetMapping("/all/movie/{movieId}")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByMovieId(@PathVariable("movieId") Long movieId) throws SQLException {
        List<Schedule> schedules=scheduleService.getScheduleByMovieId(movieId);
        List<ScheduleResponse> scheduleResponses=new ArrayList<>();
        for(Schedule schedule:schedules){
            scheduleResponses.add(scheduleService.getScheduleResponse(schedule));
        }
        return ResponseEntity.ok(scheduleResponses);
    }

    @GetMapping("/all/room/{roomId}")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByRoomId(@PathVariable("roomId") Long roomId) throws SQLException {
        List<Schedule> schedules=scheduleService.getScheduleByRoomId(roomId);
        List<ScheduleResponse> scheduleResponses=new ArrayList<>();
        for(Schedule schedule:schedules){
            scheduleResponses.add(scheduleService.getScheduleResponse(schedule));
        }
        return ResponseEntity.ok(scheduleResponses);
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> getScheduleById(@PathVariable("scheduleId") Long scheduleId){
        try{
            Schedule schedule=scheduleService.getScheduleById(scheduleId);
            ScheduleResponse scheduleResponse=scheduleService.getScheduleResponse(schedule);
            return ResponseEntity.ok(scheduleResponse);
        }catch (Exception e){
            return ResponseEntity.ok("Not found schedule.");
        }
    }
    // lay lich chieu theo rap va phim, dung cho qua trinh dat ve
    @GetMapping("/all/client/BranchAndMovie/{branchId}/{movieId}")
    public ResponseEntity<?> getSchedulesByBranchIdMovieId(@PathVariable("branchId") Long branchId
                                                    ,@PathVariable("movieId") Long movieId){
        try {
            List<Schedule> schedules=scheduleService.getSchedulesByBranchIdMovieId(branchId,movieId);
            List<ScheduleResponse> scheduleResponses=new ArrayList<>();
            for(Schedule schedule:schedules){
                scheduleResponses.add(scheduleService.getScheduleResponse(schedule));
            }
            return ResponseEntity.ok(scheduleResponses);
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }

    //phuc vu show lich chieu, de dat phong(day khong phai lay ra lich goi y dau nha)
    // dung cho them lich chieu cua admin
    @GetMapping("/getSchedulesByRoomIdDate")
    public ResponseEntity<?> getSchedulesByRoomIdDate(
            @ModelAttribute ScheduleRoomDateRequest scheduleRoomDateRequest){
        try{
            List<Schedule> schedules=scheduleService.getSchedulesByRoomIdDate(scheduleRoomDateRequest.getMovieId()
            ,scheduleRoomDateRequest.getRoomId(),scheduleRoomDateRequest.getStartDate());
            List<ScheduleResponse> scheduleResponses=new ArrayList<>();
            for(Schedule schedule:schedules){
                scheduleResponses.add(scheduleService.getScheduleResponse(schedule));
            }
            return ResponseEntity.ok(scheduleResponses);
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{scheduleId}")
    public ResponseEntity<String> deleteSchedule(@PathVariable("scheduleId") Long id){
        try {
            Schedule schedule=scheduleService.getScheduleById(id);
            if(LocalDate.now().isBefore(schedule.getStartDate()) && !scheduleService.ordered(id)){
                return scheduleService.deleteSchedule(id);
            }
            return ResponseEntity.ok("Cannot be deleted because tickets have already been booked or showtime have been made");
        }catch (Exception e){
            return ResponseEntity.ok("Error delete schedule.");
        }
    }
    @PutMapping("/update/{scheduleId}")
    public ResponseEntity<?> updateSchedule(@PathVariable("scheduleId") Long scheduleId
                                            , @ModelAttribute ScheduleRequest scheduleRequest){
        try{
            Schedule schedule=scheduleService.getScheduleById(scheduleId);
            if(schedule.getRoom().getStatus()
                    && LocalDate.now().isBefore(schedule.getStartDate())
                    && !scheduleService.ordered(scheduleId)) {
                Schedule schedule1 = scheduleService.updateSchedule(scheduleId, scheduleRequest);
                if(schedule1==null){
                    throw new Exception("Showtime clash");
                }
                ScheduleResponse scheduleResponse = scheduleService.getScheduleResponse(schedule1);
                return ResponseEntity.ok(scheduleResponse);
            }
            return ResponseEntity.ok("Cannot be updated because tickets have already been booked or showtime have been made or already booked");
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @PostMapping("/addNew/{movieId}/{roomId}")
    public ResponseEntity<String> addNewSchedule(@PathVariable("movieId") Long movieId
                                                ,@PathVariable("roomId") Long roomId
                                                ,@ModelAttribute ScheduleRequest scheduleRequest){
        try{
            Room room=roomService.getRoom(roomId);
            if(room.getStatus() && LocalDate.now().isBefore(scheduleRequest.getStartDate())){
                scheduleService.addNewSchedule(movieId,roomId,scheduleRequest);
                return ResponseEntity.ok("Add Schedule successfully.");
            }
            return ResponseEntity.ok("Error: The room is out of service or showtime in the past");
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }
}
