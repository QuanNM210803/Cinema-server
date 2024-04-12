package com.example.cinemaserver.repository;

import com.example.cinemaserver.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat,Long> {
    @Query("SELECT s FROM Seat s WHERE (s.room.id=:id) ORDER BY s.name")
    List<Seat> findSeatsByRoomId(Long id);
}
