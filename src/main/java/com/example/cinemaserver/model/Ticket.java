package com.example.cinemaserver.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double price;
    private String qr;

    @ManyToOne
    @JoinColumn(nullable = false,name = "Seatid")
    private Seat seat;

    @ManyToOne
    @JoinColumn(nullable = false,name = "Billid")
    private Bill bill;

    @ManyToOne
    @JoinColumn(nullable = false,name = "Scheduleid")
    private Schedule schedule;

    public Ticket(Double price, String qr, Seat seat, Bill bill, Schedule schedule) {
        this.price = price;
        this.qr = qr;
        this.seat = seat;
        this.bill = bill;
        this.schedule = schedule;
    }
}
