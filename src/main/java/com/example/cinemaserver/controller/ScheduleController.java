package com.example.cinemaserver.controller;

import com.example.cinemaserver.request.ScheduleRequest;
import com.example.cinemaserver.request.ScheduleRoomDateRequest;
import com.example.cinemaserver.model.Movie;
import com.example.cinemaserver.model.Room;
import com.example.cinemaserver.model.Schedule;
import com.example.cinemaserver.response.ScheduleResponse;
import com.example.cinemaserver.service.MovieService;
import com.example.cinemaserver.service.RoomService;
import com.example.cinemaserver.service.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.module.FindException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/schedules")
@AllArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final RoomService roomService;
    private final MovieService movieService;
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getSchedules() {
        List<Schedule> schedules=scheduleService.getSchedules();
        List<ScheduleResponse> scheduleResponses=new ArrayList<>();
        for(Schedule schedule:schedules){
            scheduleResponses.add(scheduleService.getScheduleResponse(schedule));
        }
        return ResponseEntity.ok(scheduleResponses);
    }
    @GetMapping("/all/movie/{movieId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getSchedulesByMovieId(@PathVariable("movieId") Long movieId) {
        try{
            List<Schedule> schedules=scheduleService.getScheduleByMovieId(movieId);
            List<ScheduleResponse> scheduleResponses=new ArrayList<>();
            for(Schedule schedule:schedules){
                scheduleResponses.add(scheduleService.getScheduleResponse(schedule));
            }
            return ResponseEntity.ok(scheduleResponses);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/all/room/{roomId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getSchedulesByRoomId(@PathVariable("roomId") Long roomId) {
        try{
            List<Schedule> schedules=scheduleService.getScheduleByRoomId(roomId);
            List<ScheduleResponse> scheduleResponses=new ArrayList<>();
            for(Schedule schedule:schedules){
                scheduleResponses.add(scheduleService.getScheduleResponse(schedule));
            }
            return ResponseEntity.ok(scheduleResponses);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{scheduleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getScheduleById(@PathVariable("scheduleId") Long scheduleId){
        try{
            Schedule schedule=scheduleService.getScheduleById(scheduleId);
            ScheduleResponse scheduleResponse=scheduleService.getScheduleResponse(schedule);
            return ResponseEntity.ok(scheduleResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    // lay lich chieu theo rap va phim, dung cho qua trinh dat ve
    @GetMapping("/all/client/BranchAndMovie/{branchId}/{movieId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //phuc vu show lich chieu, de dat phong(day khong phai lay ra lich goi y dau nha)
    // dung cho them lich chieu cua admin
    @PostMapping("/getSchedulesByRoomIdDate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getSchedulesByRoomIdDate(
            @ModelAttribute ScheduleRoomDateRequest scheduleRoomDateRequest){
        try{
            List<Schedule> schedules=scheduleService.getSchedulesByRoomIdDate(scheduleRoomDateRequest.getRoomId()
                                                                            ,scheduleRoomDateRequest.getStartDate());
            List<ScheduleResponse> scheduleResponses=new ArrayList<>();
            for(Schedule schedule:schedules){
                scheduleResponses.add(scheduleService.getScheduleResponse(schedule));
            }
            return ResponseEntity.ok(scheduleResponses);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{scheduleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteSchedule(@PathVariable("scheduleId") Long id){
        try {
            scheduleService.deleteSchedule(id);
            return ResponseEntity.ok("Delete successfully.");
        }catch (FindException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    @PutMapping("/update/{scheduleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateSchedule(@PathVariable("scheduleId") Long scheduleId
                                            , @ModelAttribute ScheduleRequest scheduleRequest){
        try{
            Schedule schedule=scheduleService.getScheduleById(scheduleId);
            if(!schedule.getRoom().getStatus()){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Can't be updated because screening room is not operating.");
            }
            if(!LocalDate.now().isBefore(schedule.getStartDate())){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Can't be updated because the show has already been shown.");
            }
            if(!LocalDate.now().isBefore(scheduleRequest.getStartDate())){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Can't be updated because showtime must be after today.");
            }
            if(!schedule.getMovie().getReleaseDate().isBefore(scheduleRequest.getStartDate().plusDays(1))){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Can't be updated because showtime must be after the movie release date.");
            }
            if(scheduleService.ordered(scheduleId)){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Can't be updated because tickets have already been booked.");
            }
            Schedule theSchedule = scheduleService.updateSchedule(scheduleId, scheduleRequest);
            ScheduleResponse scheduleResponse = scheduleService.getScheduleResponse(theSchedule);
            return ResponseEntity.ok(scheduleResponse);
        }catch (FindException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/addNew/{movieId}/{roomId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addNewSchedule(@PathVariable("movieId") Long movieId
                                                ,@PathVariable("roomId") Long roomId
                                                ,@ModelAttribute ScheduleRequest scheduleRequest){
        try{
            Room room=roomService.getRoom(roomId);
            Movie movie=movieService.getMovie(movieId);
            if(!room.getStatus()){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Can't be create showtime because screening room is not operating.");
            }
            if(!LocalDate.now().isBefore(scheduleRequest.getStartDate())){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Can't be create showtime because showtime must be after today.");
            }
            if(!movie.getReleaseDate().isBefore(scheduleRequest.getStartDate().plusDays(1))){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Can't be create showtime because showtime must be after the movie release date.");
            }
            Schedule schedule=scheduleService.addNewSchedule(movieId,roomId,scheduleRequest);
            ScheduleResponse scheduleResponse=scheduleService.getScheduleResponse(schedule);
            return ResponseEntity.ok(scheduleResponse);
        }catch (FindException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
