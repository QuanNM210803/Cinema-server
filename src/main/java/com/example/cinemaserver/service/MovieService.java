package com.example.cinemaserver.service;

import com.example.cinemaserver.Exception.ResourceNotFoundException;
import com.example.cinemaserver.Request.MovieRequest;
import com.example.cinemaserver.model.Movie;
import com.example.cinemaserver.repository.MovieRepository;
import com.example.cinemaserver.response.MovieResponse;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService implements IMovieService{
    private final MovieRepository movieRepository;
    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }
    @Override
    public Movie getMovie(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Branch not found"));
    }

    @Override
    public void addNewMovie(MovieRequest movieRequest) throws IOException, SQLException {
        Blob blob=null;
        if(!movieRequest.getPhoto().isEmpty()){
            byte[] bytes= movieRequest.getPhoto().getBytes();
            blob=new SerialBlob(bytes);
        }
        Movie movie=new Movie(movieRequest.getName(),movieRequest.getActor()
        ,movieRequest.getDirector(),movieRequest.getDescription(),movieRequest.getLanguage(),
                movieRequest.getCategory(),movieRequest.getTrailerURL(),movieRequest.getDuration()
        ,movieRequest.getReleaseDate(),blob);
        movieRepository.save(movie);
    }

    @Override
    public ResponseEntity<String> deleteMovieById(Long id) {
        try{
            Movie movie=movieRepository.findById(id).get();
            movieRepository.deleteById(id);
            return ResponseEntity.ok("Delete successfully.");
        }catch (Exception e){
            throw new ResourceNotFoundException("Error fetching movie.");
        }
    }

    @Override
    public Movie updateMovie(Long id, MovieRequest movieRequest) throws IOException, SQLException {
        Movie movie=movieRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Movie not found."));
        if(movieRequest.getName()!=null){
            movie.setName(movieRequest.getName());
        }
        if(movieRequest.getActor()!=null){
            movie.setActor(movieRequest.getActor());
        }
        if(movieRequest.getDirector()!=null){
            movie.setDirector(movieRequest.getDirector());
        }
        if(movieRequest.getDescription()!=null){
            movie.setDescription(movieRequest.getDescription());
        }
        if(movieRequest.getLanguage()!=null){
            movie.setLanguage(movieRequest.getLanguage());
        }
        if(movieRequest.getCategory()!=null){
            movie.setCategory(movieRequest.getCategory());
        }
        if(movieRequest.getTrailerURL()!=null){
            movie.setTrailerURL(movieRequest.getTrailerURL());
        }
        if(movieRequest.getDuration()!=null){
            movie.setDuration(movieRequest.getDuration());
        }
        if(movieRequest.getReleaseDate()!=null){
            movie.setReleaseDate(movieRequest.getReleaseDate());
        }
        if(!movieRequest.getPhoto().isEmpty()){
            byte[] bytes=movieRequest.getPhoto().getBytes();
            Blob blob=new SerialBlob(bytes);
            movie.setPhoto(blob);
        }
        return movieRepository.save(movie);
    }

    @Override
    public List<Movie> getMoviesClient() {
        return movieRepository.findMoviesClient(LocalDate.now(), LocalTime.now());
    }


    @Override
    public String getMoviePhoto(Movie movie) throws SQLException {
        Blob blob=movie.getPhoto();
        byte[] photoBytes = blob!=null ? blob.getBytes(1,(int)blob.length()):null;
        String base64photo = photoBytes!=null && photoBytes.length>0 ?  Base64.encodeBase64String(photoBytes):"";
        return base64photo;
    }
    @Override
    public MovieResponse getMovieResponse(Movie movie) throws SQLException {
        DateTimeFormatter formatDate= DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return new MovieResponse(movie.getId(), movie.getName(),
                movie.getActor(), movie.getDirector(),
                movie.getDescription(), movie.getLanguage(),
                movie.getCategory(), movie.getTrailerURL(),
                movie.getDuration(), movie.getReleaseDate().format(formatDate),
                getMoviePhoto(movie));
    }

}
