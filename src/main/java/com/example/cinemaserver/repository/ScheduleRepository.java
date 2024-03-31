package com.example.cinemaserver.repository;

import com.example.cinemaserver.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule,Long> {
    @Query("SELECT s FROM Schedule s WHERE (s.movie.id=:movieId) ")
    List<Schedule> findSchedulesByMovieId(Long movieId);
    @Query("SELECT s FROM Schedule s WHERE (s.room.id=:roomId) ")
    List<Schedule> findSchedulesByRoomId(Long roomId);
    @Query("SELECT s FROM Schedule s WHERE ((s.startDate=:startDate) " +
            "AND (s.room.id=:roomId)) " +
            "ORDER BY s.startTime")
    List<Schedule> findSchedulesByRoomIdDate(Long roomId, LocalDate startDate);
    @Query("SELECT s FROM Schedule s WHERE s.movie.id=:movieId " +
            "AND s.room.id IN (SELECT r.id FROM Room r WHERE r.branch.id=:branchId AND r.status=true)" +
            "AND ((s.startDate>:date) OR (s.startDate=:date AND s.startTime>:time))")
    List<Schedule> findSchedulesByBranchIdMovieId(Long branchId, Long movieId, LocalDate date, LocalTime time);
}
