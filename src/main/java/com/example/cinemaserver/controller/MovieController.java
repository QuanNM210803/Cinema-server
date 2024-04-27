package com.example.cinemaserver.controller;

import com.example.cinemaserver.request.MovieRequest;
import com.example.cinemaserver.model.Movie;
import com.example.cinemaserver.response.MovieResponse;
import com.example.cinemaserver.service.IMovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {
    private final IMovieService movieService;
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<MovieResponse>> getAllMovies() throws SQLException {
        List<Movie> movies=movieService.getAllMovies();
        return ResponseEntity.ok(getListMovieResponse(movies));
    }
    @GetMapping("/all/client")
    public ResponseEntity<?> getMoviesClient() throws SQLException {
        List<Movie> movies=movieService.getMoviesClient();
        return ResponseEntity.ok(getListMovieResponse(movies));
    }
    @GetMapping("/all/upcoming")
    public ResponseEntity<?> getMoviesUpcoming() throws SQLException {
            List<Movie> movies=movieService.getMoviesUpcoming();
            return ResponseEntity.ok(getListMovieResponse(movies));
    }
    public List<MovieResponse> getListMovieResponse(List<Movie> movies) throws SQLException {
        List<MovieResponse> movieResponses=new ArrayList<>();
        for(Movie movie:movies){
            movieResponses.add(movieService.getMovieResponse(movie));
        }
        return movieResponses;
    }
    @GetMapping("/{movieId}")
    public ResponseEntity<?> getMovieById(@PathVariable("movieId") Long id){
        try{
            Movie movie=movieService.getMovie(id);
            MovieResponse movieResponse=movieService.getMovieResponse(movie);
            return ResponseEntity.ok(movieResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found movie.");
        }
    }
    @PostMapping("/addNew")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addNewMovie(@Valid @ModelAttribute MovieRequest movieRequest) throws SQLException, IOException {
        Movie movie=movieService.addNewMovie(movieRequest);
        MovieResponse movieResponse=movieService.getMovieResponse(movie);
        return ResponseEntity.ok(movieResponse);
    }

//    viet de day thoi, chu dung ko dc xoa movie, neu xoa se anh huong cac ban khac
//    trog khi cung ko nhat thiet phai xoa
    @DeleteMapping("/delete/{movieId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteMovie(@PathVariable("movieId") Long id){
        try {
            movieService.deleteMovieById(id);
            return ResponseEntity.ok("Delete successfully.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/update/{movieId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateMovie(@PathVariable("movieId") Long id
                                                    ,@ModelAttribute MovieRequest movieRequest) {
        try{
            Movie movie=movieService.updateMovie(id,movieRequest);
            MovieResponse movieResponse=movieService.getMovieResponse(movie);
            return ResponseEntity.ok(movieResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
