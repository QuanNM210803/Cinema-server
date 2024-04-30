package com.example.cinemaserver.repository;

import com.example.cinemaserver.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BranchRepository extends JpaRepository<Branch,Long> {
//    @Query("SELECT b FROM Branch b WHERE b.area.id=:areaId AND " +
//            "b.id IN (SELECT DISTINCT (r.branch.id) FROM Room r WHERE r.id IN (SELECT DISTINCT (s.room.id) FROM Schedule s WHERE " +
//            "s.room.status=true AND s.movie.id=:movieId AND ((s.startDate>:date) OR (s.startDate=:date AND s.startTime>:time))))")
    @Query("SELECT b FROM Branch b WHERE b.area.id=:areaId AND " +
            "b.id IN (SELECT DISTINCT (s.room.branch.id) FROM Schedule s WHERE s.movie.id=:movieId AND s.room.status=true AND " +
            "((s.startDate>:date) OR (s.startDate=:date AND s.startTime>:time)))")
    List<Branch> findBranchClientByMovieIdAndAreaId(Long movieId, Long areaId, LocalDate date, LocalTime time);
    @Query("SELECT b FROM Branch b WHERE b.area.id=:areaId")
    List<Branch> findBranchByAreaId(Long areaId);
}
