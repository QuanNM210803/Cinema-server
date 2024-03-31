package com.example.cinemaserver.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdTime;

    @ManyToOne
    @JoinColumn(nullable = false,name = "Userid")
    private User user;

    public Bill(LocalDateTime createdTime, User user) {
        this.createdTime = createdTime;
        this.user = user;
    }
}
