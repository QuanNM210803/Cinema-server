package com.example.cinemaserver.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MovieRequest {
    private String name;
    private String actor;
    private String director;
    private String description;
    private String language;
    private String category;
    private String trailerURL;
    private Integer duration;
    private LocalDate releaseDate;
    private MultipartFile photo;
}
