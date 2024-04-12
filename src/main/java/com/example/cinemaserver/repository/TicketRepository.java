package com.example.cinemaserver.repository;

import com.example.cinemaserver.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket,Long> {
    @Query("SELECT t FROM Ticket t WHERE t.bill.id=:billId ORDER BY t.seat.name")
    List<Ticket> findByBillId(Long billId);
    @Query("SELECT t FROM Ticket t WHERE t.schedule.id=:scheduleId ORDER BY t.seat.name")
    List<Ticket> findByScheduleId(Long scheduleId);
    @Query("SELECT t FROM Ticket t WHERE t.schedule.movie.id=:movieId")
    List<Ticket> findTicketsByMovieId(Long movieId);

    @Query("SELECT t FROM Ticket t WHERE t.schedule.room.branch.id=:branchId")
    List<Ticket> findTicketsByBranchId(Long branchId);
}
