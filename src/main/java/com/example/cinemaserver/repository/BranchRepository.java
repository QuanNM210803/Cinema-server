package com.example.cinemaserver.repository;

import com.example.cinemaserver.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BranchRepository extends JpaRepository<Branch,Long> {
    @Query("SELECT b FROM Branch b WHERE " +
            "b.id IN (SELECT DISTINCT (r.branch.id) FROM Room r WHERE r.id IN (SELECT DISTINCT (s.room.id) FROM Schedule s WHERE " +
            "s.room.status=true AND s.movie.id=:movieId AND ((s.startDate>:date) OR (s.startDate=:date AND s.startTime>:time))))")
    List<Branch> findBranchClientByMovieId(Long movieId, LocalDate date, LocalTime time);
}
