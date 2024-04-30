package com.example.cinemaserver.repository;

import com.example.cinemaserver.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill,Long> {
    @Query("SELECT b FROM Bill b WHERE b.id IN " +
            "(SELECT DISTINCT (t.bill.id) FROM Ticket t WHERE t.schedule.id=:scheduleId)" +
            "ORDER BY b.createdTime DESC ")
    List<Bill> findByScheduleId(Long scheduleId);

    @Query("SELECT b FROM Bill b WHERE b.user.id=:userId " +
            "ORDER BY b.createdTime DESC ")
    List<Bill> findByUserId(Long userId);

    @Query("SELECT b FROM Bill b WHERE DATE (b.createdTime)=:currentDate " +
            "AND b.id IN (SELECT DISTINCT (t.bill.id) FROM Ticket t WHERE" +
            "(:movieId IS NULL OR t.schedule.movie.id=:movieId) AND (:branchId IS NULL OR t.schedule.room.branch.id=:branchId))" +
            "ORDER BY b.createdTime DESC ")
    List<Bill> findBillsByDate_MovieId_BranchId(LocalDate currentDate, Long movieId, Long branchId);

//    @Query("SELECT b FROM Bill b WHERE " +
//            "b.id IN (SELECT DISTINCT (t.bill.id) FROM Ticket t WHERE t.schedule.id IN " +
//            "(SELECT s.id FROM Schedule s WHERE (:movieId IS NULL OR s.movie.id=:movieId) AND (:branchId IS NULL OR s.room.branch.id=:branchId)))" +
//            "ORDER BY b.createdTime DESC ")
    @Query("SELECT b FROM Bill b WHERE " +
            "b.id IN (SELECT DISTINCT (t.bill.id) FROM Ticket t WHERE " +
            "(:movieId IS NULL OR t.schedule.movie.id=:movieId) AND (:branchId IS NULL OR t.schedule.room.branch.id=:branchId))" +
            "ORDER BY b.createdTime DESC ")
    List<Bill> findBillsByMovieId_BranchId(Long movieId, Long branchId);
}
