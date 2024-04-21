package com.example.cinemaserver.service;

import com.example.cinemaserver.request.SeatRequest;
import com.example.cinemaserver.model.Room;
import com.example.cinemaserver.model.Seat;
import com.example.cinemaserver.repository.SeatRepository;
import com.example.cinemaserver.response.RoomResponse;
import com.example.cinemaserver.response.SeatResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.lang.module.FindException;
import java.util.List;

@Service
@AllArgsConstructor
public class SeatService implements ISeatService{
    private final SeatRepository seatRepository;
    private final RoomService roomService;
    @Override
    public List<Seat> findSeatsByRoomId(Long id) {
        roomService.getRoom(id);
        return seatRepository.findSeatsByRoomId(id);
    }
    @Override
    public Seat getSeatBySeatId(Long id) {
        return seatRepository.findById(id).orElseThrow(()->new FindException("Not found seat."));
    }

    @Override
    public Seat addNewSeat(SeatRequest seatRequest, Room room) {
        List<Seat> seats=seatRepository.findSeatsByRoomId(room.getId());
        List<String> seatNames=seats.stream().map(Seat::getName).toList();
        if(seatNames.contains(seatRequest.getName())){
            throw new RuntimeException(seatRequest.getName()+" already exists.");
        }
        Seat seat=new Seat(seatRequest.getName(),seatRequest.getPrice(),room);
        return seatRepository.save(seat);
    }

    @Override
    public Seat updateSeat(Long id, SeatRequest seatRequest) {
        Seat seat=this.getSeatBySeatId(id);
        List<Seat> seats=seatRepository.findSeatsByRoomId(seat.getRoom().getId());
        List<String> seatNames=seats.stream().map(Seat::getName).toList();
        if(seatNames.contains(seatRequest.getName()) && !seatRequest.getName().equals(seat.getName())){
            throw new RuntimeException(seatRequest.getName()+" already exists.");
        }
        if(!StringUtils.isBlank(seatRequest.getName())){
            seat.setName(seatRequest.getName());
        }
        if(seatRequest.getPrice()!=null){
            seat.setPrice(seatRequest.getPrice());
        }
        return seatRepository.save(seat);
    }

    @Override
    public SeatResponse getSeatResponse(Seat seat) {
        Room room=seat.getRoom();
        RoomResponse roomResponse=roomService.getRoomResponseNonePhoto(room);
        return new SeatResponse(seat.getId(),seat.getName(), seat.getPrice(), roomResponse);
    }
}
