package com.example.cinemaserver.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponse {
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
    private String photo;
}
