package com.example.cinemaserver.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Blob;
import java.time.LocalDate;

@Data
@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String actor;
    private String director;
    private String description;
    private String language;
    private String category;
    private String trailerURL;
    private Integer duration;
    private LocalDate releaseDate;
    @Lob
    private Blob photo;
    public Movie(){

    }

    public Movie(String name, String actor
            , String director, String description
            , String language, String category
            , String trailerURL, Integer duration
            , LocalDate releaseDate, Blob photo) {
        this.name = name;
        this.actor = actor;
        this.director = director;
        this.description = description;
        this.language = language;
        this.category = category;
        this.trailerURL = trailerURL;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.photo = photo;
    }
}
