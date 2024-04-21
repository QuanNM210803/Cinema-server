package com.example.cinemaserver.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Blob;

@Entity
@Data
@AllArgsConstructor
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String introduction;
    @Lob
    private Blob photo;
    private Boolean status=true;
    @ManyToOne
    @JoinColumn(nullable = false,name = "Areaid")
    private Area area;
    public Branch() {
    }

}
