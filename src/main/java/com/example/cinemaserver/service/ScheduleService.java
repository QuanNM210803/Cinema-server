package com.example.cinemaserver.service;

import com.example.cinemaserver.request.ScheduleRequest;
import com.example.cinemaserver.model.*;
import com.example.cinemaserver.repository.ScheduleRepository;
import com.example.cinemaserver.repository.SeatRepository;
import com.example.cinemaserver.repository.Seat_ScheduleRepository;
import com.example.cinemaserver.repository.TicketRepository;
import com.example.cinemaserver.response.MovieResponse;
import com.example.cinemaserver.response.RoomResponse;
import com.example.cinemaserver.response.ScheduleResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.module.FindException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ScheduleService implements IScheduleService{
    private final ScheduleRepository scheduleRepository;
    private final MovieService movieService;
    private final RoomService roomService;
    private final BranchService branchService;
    private final SeatRepository seatRepository;
    private final Seat_ScheduleRepository seat_scheduleRepository;
    private final TicketRepository ticketRepository;

    @Override
    public List<Schedule> getSchedules() {
        return scheduleRepository.findAll();
    }
    @Override
    public Schedule getScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(()->new FindException("Not found schedule."));
    }

    @Override
    public List<Schedule> getScheduleByMovieId(Long movieId) {
        movieService.getMovie(movieId);
        return scheduleRepository.findSchedulesByMovieId(movieId);
    }

    @Override
    public List<Schedule> getScheduleByRoomId(Long roomId) {
        roomService.getRoom(roomId);
        return scheduleRepository.findSchedulesByRoomId(roomId);
    }
    @Override
    public List<Schedule> getSchedulesByRoomIdDate(Long roomId, LocalDate startDate) {
        roomService.getRoom(roomId);
        List<Schedule> schedules=scheduleRepository.findSchedulesByRoomIdDate(roomId,startDate);
        return schedules;
    }


    @Override
    public void deleteSchedule(Long id) {
        Schedule schedule=this.getScheduleById(id);
        if(!LocalDate.now().isBefore(schedule.getStartDate())){
            throw new RuntimeException("Can't be deleted because the show has already been shown.");
        }
        if(this.ordered(id)){
            throw new RuntimeException("Can't be deleted because tickets have already been booked.");
        }
        scheduleRepository.deleteById(id);
    }

    @Override
    public Schedule updateSchedule(Long scheduleId, ScheduleRequest scheduleRequest) throws Exception {
        Schedule schedule=this.getScheduleById(scheduleId);
        if(checkScheduleTimeUpdate(schedule,scheduleRequest)){
            if(scheduleRequest.getStartDate()!=null){
                schedule.setStartDate(scheduleRequest.getStartDate());
            }
            if(scheduleRequest.getStartTime()!=null){
                schedule.setStartTime(scheduleRequest.getStartTime());
            }
            return scheduleRepository.save(schedule);
        }
        throw new Exception("Showtime clash.");
    }

    @Override
    public Schedule addNewSchedule(Long movieId, Long roomId, ScheduleRequest scheduleRequest) throws Exception {

        if(checkScheduleTimeAdd(scheduleRequest.getStartDate(),scheduleRequest.getStartTime()
        ,movieService.getMovie(movieId),roomService.getRoom(roomId))){
            List<Seat> seats=seatRepository.findSeatsByRoomId(roomId);
            Schedule schedule=new Schedule(scheduleRequest.getStartDate(),scheduleRequest.getStartTime()
                    ,(long) seats.size()
                    ,movieService.getMovie(movieId),roomService.getRoom(roomId));
            Schedule theSchedule=scheduleRepository.save(schedule);
            for(Seat seat:seats){
                Seat_Schedule seatSchedule=new Seat_Schedule(false,seat.getPrice(),seat,theSchedule);
                seat_scheduleRepository.save(seatSchedule);
            }
            return theSchedule;
        }
        throw new Exception("Showtime clash.");
    }

    @Override
    public boolean ordered(Long scheduleId) {
        List<Boolean> ordered=seat_scheduleRepository.findOrderedByScheduleId(scheduleId);
        return ordered.contains(true);
    }

    @Override
    public List<Schedule> getSchedulesByBranchIdMovieId(Long branchId,Long movieId) {
        branchService.getBranch(branchId);
        movieService.getMovie(movieId);
        return scheduleRepository.findSchedulesByBranchIdMovieId(branchId,movieId,LocalDate.now(),LocalTime.now());
    }

    private boolean checkScheduleTimeAdd(LocalDate startDate,LocalTime startTime,
                                         Movie movie,Room room) {
        List<Schedule> schedules=scheduleRepository.findSchedulesByRoomIdDate(room.getId(),startDate);
        LocalTime newStartTime=startTime;
        int duration=movie.getDuration();
        LocalTime newEndTime=newStartTime.plusMinutes(duration);
        for(Schedule sche:schedules){
            LocalTime oldStartTime=sche.getStartTime();
            LocalTime oldEndTime=oldStartTime.plusMinutes(sche.getMovie().getDuration());
            if(!(newEndTime.isBefore(oldStartTime) || oldEndTime.isBefore(newStartTime))){
                return false;
            }
        }
        return true;
    }


    private boolean checkScheduleTimeUpdate(Schedule schedule, ScheduleRequest scheduleRequest) {
        List<Schedule> schedules=scheduleRepository.findSchedulesByRoomIdDate(schedule.getRoom().getId(),scheduleRequest.getStartDate());
        if(LocalDate.now().isBefore(scheduleRequest.getStartDate())){
            LocalTime newStartTime=scheduleRequest.getStartTime();
            int duration=schedule.getMovie().getDuration();
            LocalTime newEndTime=newStartTime.plusMinutes(duration);
            for(Schedule sche:schedules){
                if(!Objects.equals(sche.getId(), schedule.getId())){
                    LocalTime oldStartTime=sche.getStartTime();
                    LocalTime oldEndTime=oldStartTime.plusMinutes(sche.getMovie().getDuration());
                    if(!(newEndTime.isBefore(oldStartTime) || oldEndTime.isBefore(newStartTime))){
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public ScheduleResponse getScheduleResponse(Schedule schedule) {
        Movie movie=schedule.getMovie();
        MovieResponse movieResponse=movieService.getMovieResponseNonePhoto(movie);
        Room room=schedule.getRoom();
        RoomResponse roomResponse=roomService.getRoomResponseNonePhoto(room);
        DateTimeFormatter formatDate= DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatTime= DateTimeFormatter.ofPattern("HH:mm");

        List<Ticket> tickets=ticketRepository.findByScheduleId(schedule.getId());
        LocalTime startTime=schedule.getStartTime();
        LocalTime endTime=startTime.plusMinutes(movie.getDuration());
        return new ScheduleResponse(schedule.getId()
                                    ,schedule.getStartDate().format(formatDate)
                                    ,startTime.format(formatTime)
                                    ,endTime.format(formatTime)
                                    ,tickets.stream().mapToDouble(Ticket::getPrice).sum()
                                    , (long) tickets.size()
                                    ,schedule.getNumberOfSeats()
                                    ,movieResponse
                                    ,roomResponse);
    }
}