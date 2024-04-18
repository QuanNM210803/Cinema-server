package com.example.cinemaserver.service;

import com.example.cinemaserver.request.MovieRequest;
import com.example.cinemaserver.model.Movie;
import com.example.cinemaserver.response.MovieResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IMovieService {
    List<Movie> getAllMovies();

    MovieResponse getMovieResponse(Movie movie) throws SQLException;

    String getMoviePhoto(Movie movie) throws SQLException;

    Movie getMovie(Long id);

    Movie addNewMovie(MovieRequest movieRequest) throws IOException, SQLException;

    ResponseEntity<String> deleteMovieById(Long id);


    Movie updateMovie(Long id, MovieRequest movieRequest) throws IOException, SQLException;

    List<Movie> getMoviesClient();

    MovieResponse getMovieResponseNonePhoto(Movie movie) throws SQLException;

    List<Movie> getMoviesUpcoming();
}
