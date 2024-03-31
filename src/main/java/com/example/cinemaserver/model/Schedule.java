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

    @ManyToOne
    @JoinColumn(name = "Movieid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "Roomid",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Room room;

    public Schedule(LocalDate startDate, LocalTime startTime, Movie movie, Room room) {
        this.startDate = startDate;
        this.startTime = startTime;
        this.movie = movie;
        this.room = room;
    }
}
