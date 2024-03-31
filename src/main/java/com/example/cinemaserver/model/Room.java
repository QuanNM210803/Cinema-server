package com.example.cinemaserver.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Blob;

@Data
@Entity
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Boolean status=true;
    @Lob
    private Blob photo;
    @ManyToOne
    @JoinColumn(nullable = false,name = "Branchid")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Branch branch;

    public Room(String name, Blob photo, Branch branch) {
        this.name = name;
        this.photo = photo;
        this.branch = branch;
    }
}
