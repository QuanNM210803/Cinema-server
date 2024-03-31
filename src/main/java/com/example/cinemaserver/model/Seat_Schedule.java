package com.example.cinemaserver.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@Entity
public class Seat_Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean ordered;
    private Double price;

    @ManyToOne
    @JoinColumn(name = "Seatid",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "Scheduleid",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Schedule schedule;

    public Seat_Schedule(Boolean ordered, Double price, Seat seat, Schedule schedule) {
        this.ordered = ordered;
        this.price = price;
        this.seat = seat;
        this.schedule = schedule;
    }

    public Seat_Schedule() {

    }
}
