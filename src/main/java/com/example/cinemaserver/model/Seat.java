package com.example.cinemaserver.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Data
@Entity
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double price;

    @ManyToOne
    @JoinColumn(nullable = false,name = "Roomid")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Room room;

    public Seat(String name, Double price, Room room) {
        this.name = name;
        this.price = price;
        this.room = room;
    }

    public Seat() {

    }
}
