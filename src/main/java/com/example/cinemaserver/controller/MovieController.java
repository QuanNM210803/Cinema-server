package com.example.cinemaserver.controller;

import com.example.cinemaserver.Request.MovieRequest;
import com.example.cinemaserver.model.Movie;
import com.example.cinemaserver.response.MovieResponse;
import com.example.cinemaserver.service.IMovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<MovieResponse>> getAllMovies() throws SQLException {
        List<Movie> movies=movieService.getAllMovies();
        return ResponseEntity.ok(getListMovieResponse(movies));
    }

    @GetMapping("/all/client")
    public ResponseEntity<?> getMoviesClient(){
        try{
            List<Movie> movies=movieService.getMoviesClient();
            return ResponseEntity.ok(getListMovieResponse(movies));
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }
    @GetMapping("/all/upcoming")
    public ResponseEntity<?> getMoviesUpcoming(){
        try{
            List<Movie> movies=movieService.getMoviesUpcoming();
            return ResponseEntity.ok(getListMovieResponse(movies));
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching movie.");
        }
    }
    @PostMapping("/addNew")
    public ResponseEntity<?> addNewMovie(@Valid @ModelAttribute MovieRequest movieRequest){
        try{
            movieService.addNewMovie(movieRequest);
            return ResponseEntity.ok("Add movie successfully");
        }catch (Exception e){
            throw new RuntimeException("Error");
        }
    }

//    viet de day thoi, chu dung ko dc xoa movie, neu xoa se anh huong cac ban khac
//    trog khi cung ko nhat thiet phai xoa
    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovie(@PathVariable("movieId") Long id){
        return movieService.deleteMovieById(id);
    }

    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieResponse> updateMovie(@PathVariable("movieId") Long id
                                                    ,@ModelAttribute MovieRequest movieRequest) throws SQLException, IOException {
        Movie movie=movieService.updateMovie(id,movieRequest);
        MovieResponse movieResponse=movieService.getMovieResponse(movie);
        return ResponseEntity.ok(movieResponse);
    }
}
