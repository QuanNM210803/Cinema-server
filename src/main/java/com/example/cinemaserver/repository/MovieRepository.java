package com.example.cinemaserver.repository;

import com.example.cinemaserver.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie,Long> {
    @Query("SELECT m FROM Movie m WHERE " +
            "m.id IN (SELECT DISTINCT (s.movie.id) FROM Schedule s WHERE s.room.status=true AND " +
            "((s.startDate>:date) OR (s.startDate=:date AND s.startTime>:time)))" +
            "ORDER BY m.releaseDate DESC ")
    List<Movie> findMoviesClient(LocalDate date, LocalTime time);
    @Query("SELECT m FROM Movie m WHERE m.releaseDate>:dateNow ORDER BY m.releaseDate DESC")
    List<Movie> findMoviesUpcoming(LocalDate dateNow);
}
