package com.example.cinemaserver.repository;

import com.example.cinemaserver.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule,Long> {
    @Query("SELECT s FROM Schedule s WHERE (s.movie.id=:movieId) " +
            "ORDER BY s.startDate DESC , s.startTime DESC ")
    List<Schedule> findSchedulesByMovieId(Long movieId);
    @Query("SELECT s FROM Schedule s WHERE (s.room.id=:roomId) " +
            "ORDER BY s.startDate DESC , s.startTime DESC ")
    List<Schedule> findSchedulesByRoomId(Long roomId);
    @Query("SELECT s FROM Schedule s WHERE ((s.startDate=:startDate) AND (s.room.id=:roomId)) " +
            "ORDER BY s.startTime")
    List<Schedule> findSchedulesByRoomIdDate(Long roomId, LocalDate startDate);
    @Query("SELECT s FROM Schedule s WHERE s.movie.id=:movieId " +
            "AND s.room.branch.id=:branchId AND s.room.status=true " +
            "AND ((s.startDate>:date) OR (s.startDate=:date AND s.startTime>:time))" +
            "ORDER BY s.startDate , s.startTime ")
    List<Schedule> findSchedulesByBranchIdMovieId(Long branchId, Long movieId, LocalDate date, LocalTime time);
    @Query("SELECT s FROM Schedule s WHERE s.room.id=:roomId " +
            "AND ((s.startDate>:date) OR (s.startDate=:date AND s.startTime>:time))")
    List<Schedule> findSchedulesFutureByRoom(Long roomId, LocalDate date, LocalTime time);
    @Query("SELECT s FROM Schedule s WHERE s.room.branch.id=:branchId " +
            "AND ((s.startDate>:date) OR (s.startDate=:date AND s.startTime>:time))")
    List<Schedule> findSchedulesFutureByBranch(Long branchId, LocalDate date, LocalTime time);
}
