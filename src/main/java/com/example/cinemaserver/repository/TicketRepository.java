package com.example.cinemaserver.repository;

import com.example.cinemaserver.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket,Long> {
    @Query("SELECT t FROM Ticket t WHERE t.bill.id=:billId")
    List<Ticket> findByBillId(Long billId);
    @Query("SELECT t FROM Ticket t WHERE t.schedule.id=:scheduleId")
    List<Ticket> findByScheduleId(Long scheduleId);
}
