package com.example.cinemaserver.repository;

import com.example.cinemaserver.model.Seat_Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface Seat_ScheduleRepository extends JpaRepository<Seat_Schedule,Long> {
    @Query("SELECT DISTINCT (ss.ordered) FROM Seat_Schedule ss WHERE ss.schedule.id=:scheduleId")
    List<Boolean> findOrderedByScheduleId(Long scheduleId);
    @Query("SELECT ss FROM Seat_Schedule ss WHERE ss.schedule.id=:scheduleId ORDER BY ss.seat.name ")
    List<Seat_Schedule> findSeat_ScheduleByScheduleId(Long scheduleId);
    @Query("SELECT ss FROM Seat_Schedule ss WHERE ss.schedule.id=:scheduleId AND ss.seat.id=:seatId")
    Seat_Schedule findSeat_ScheduleByScheduleIdAndSeatId(Long scheduleId, Long seatId);
}
