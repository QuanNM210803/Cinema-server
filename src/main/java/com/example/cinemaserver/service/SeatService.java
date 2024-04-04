package com.example.cinemaserver.service;

import com.example.cinemaserver.Exception.ResourceNotFoundException;
import com.example.cinemaserver.Request.SeatRequest;
import com.example.cinemaserver.model.Room;
import com.example.cinemaserver.model.Seat;
import com.example.cinemaserver.repository.SeatRepository;
import com.example.cinemaserver.response.RoomResponse;
import com.example.cinemaserver.response.SeatResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
@AllArgsConstructor
public class SeatService implements ISeatService{
    private final SeatRepository seatRepository;
    private final RoomService roomService;
    @Override
    public List<Seat> findSeatsByRoomId(Long id) {
        return seatRepository.findSeatsByRoomId(id);
    }
    @Override
    public Object getSeatBySeatId(Long id) {
        try{
            Seat seat=seatRepository.findById(id).get();
            SeatResponse seatResponse=this.getSeatResponse(seat);
            return seatResponse;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public void addNewSeat(SeatRequest seatRequest, Room room) {
        Seat seat=new Seat(seatRequest.getName(),seatRequest.getPrice(),room);
        seatRepository.save(seat);
    }

    @Override
    public Seat updateSeat(Long id, SeatRequest seatRequest) {
        Seat seat=seatRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Not found seat."));
        if(seatRequest.getName()!=null){
            seat.setName(seatRequest.getName());
        }
        if(seatRequest.getPrice()!=null){
            seat.setPrice(seatRequest.getPrice());
        }
        return seatRepository.save(seat);
    }

    @Override
    public SeatResponse getSeatResponse(Seat seat) throws SQLException {
        Room room=seat.getRoom();
        RoomResponse roomResponse=roomService.getRoomResponse(room);
        return new SeatResponse(seat.getId(),seat.getName(), seat.getPrice(), roomResponse);
    }


}
