package com.example.cinemaserver.service;

import com.example.cinemaserver.Request.MovieRequest;
import com.example.cinemaserver.model.Movie;
import com.example.cinemaserver.response.MovieResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface IMovieService {
    List<Movie> getAllMovies();

    MovieResponse getMovieResponse(Movie movie) throws SQLException;

    String getMoviePhoto(Movie movie) throws SQLException;

    Movie getMovie(Long id);

    void addNewMovie(MovieRequest movieRequest) throws IOException, SQLException;

    ResponseEntity<String> deleteMovieById(Long id);


    Movie updateMovie(Long id, MovieRequest movieRequest) throws IOException, SQLException;

    List<Movie> getMoviesClient();
}
