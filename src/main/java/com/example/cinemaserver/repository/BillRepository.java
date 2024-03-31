package com.example.cinemaserver.repository;

import com.example.cinemaserver.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill,Long> {
    @Query("SELECT b FROM Bill b WHERE b.id IN " +
            "(SELECT DISTINCT (t.bill.id) FROM Ticket t WHERE t.schedule.id=:scheduleId)")
    List<Bill> findByScheduleId(Long scheduleId);

    @Query("SELECT b FROM Bill b WHERE b.user.id=:userId")
    List<Bill> findByUserId(Long userId);
}
