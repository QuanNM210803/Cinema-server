package com.example.cinemaserver.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
@Entity
@NoArgsConstructor

public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate startDate;
    private LocalTime startTime;
    private Long numberOfSeats;
    @ManyToOne
    @JoinColumn(name = "Movieid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "Roomid",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Room room;

    public Schedule(LocalDate startDate, LocalTime startTime, Long numberOfSeats, Movie movie, Room room) {
        this.startDate = startDate;
        this.startTime = startTime;
        this.numberOfSeats = numberOfSeats;
        this.movie = movie;
        this.room = room;
    }
}
