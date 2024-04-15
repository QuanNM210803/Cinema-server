package com.example.cinemaserver.service;

import com.example.cinemaserver.exception.ResourceNotFoundException;
import com.example.cinemaserver.request.MovieRequest;
import com.example.cinemaserver.model.Movie;
import com.example.cinemaserver.model.Ticket;
import com.example.cinemaserver.repository.MovieRepository;
import com.example.cinemaserver.repository.TicketRepository;
import com.example.cinemaserver.response.MovieResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


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
    private final TicketRepository ticketRepository;
    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }
    @Override
    public Movie getMovie(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Movie not found"));
    }

    @Override
    public void addNewMovie(MovieRequest movieRequest) throws IOException, SQLException {
        Blob blob=null;
        if(movieRequest.getPhoto()!=null && !movieRequest.getPhoto().isEmpty()){
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
        if(!StringUtils.isBlank(movieRequest.getName())){
            movie.setName(movieRequest.getName());
        }
        if(!StringUtils.isBlank(movieRequest.getActor())){
            movie.setActor(movieRequest.getActor());
        }
        if(!StringUtils.isBlank(movieRequest.getDirector())){
            movie.setDirector(movieRequest.getDirector());
        }
        if(!StringUtils.isBlank(movieRequest.getDescription())){
            movie.setDescription(movieRequest.getDescription());
        }
        if(!StringUtils.isBlank(movieRequest.getLanguage())){
            movie.setLanguage(movieRequest.getLanguage());
        }
        if(!StringUtils.isBlank(movieRequest.getCategory())){
            movie.setCategory(movieRequest.getCategory());
        }
        if(!StringUtils.isBlank(movieRequest.getTrailerURL())){
            movie.setTrailerURL(movieRequest.getTrailerURL());
        }
        if(movieRequest.getDuration()!=null){
            movie.setDuration(movieRequest.getDuration());
        }
        if(movieRequest.getReleaseDate()!=null){
            movie.setReleaseDate(movieRequest.getReleaseDate());
        }
        if(movieRequest.getPhoto()!=null && !movieRequest.getPhoto().isEmpty()){
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
    public List<Movie> getMoviesUpcoming() {
        return movieRepository.findMoviesUpcoming(LocalDate.now());
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
        List<Ticket> tickets=ticketRepository.findTicketsByMovieId(movie.getId());
        return new MovieResponse(movie.getId(), movie.getName(),
                movie.getActor(), movie.getDirector(),
                movie.getDescription(), movie.getLanguage(),
                movie.getCategory(), movie.getTrailerURL(),
                movie.getDuration(), movie.getReleaseDate().format(formatDate),
                tickets.stream().mapToDouble(Ticket::getPrice).sum(),
                (long) tickets.size(),
                getMoviePhoto(movie));
    }
    @Override
    public MovieResponse getMovieResponseNonePhoto(Movie movie) throws SQLException {
        DateTimeFormatter formatDate= DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<Ticket> tickets=ticketRepository.findTicketsByMovieId(movie.getId());
        return new MovieResponse(movie.getId(), movie.getName(),
                movie.getActor(), movie.getDirector(),
                movie.getDescription(), movie.getLanguage(),
                movie.getCategory(), movie.getTrailerURL(),
                movie.getDuration(), movie.getReleaseDate().format(formatDate),
                tickets.stream().mapToDouble(Ticket::getPrice).sum(),
                (long) tickets.size(),
                null);
    }

}
