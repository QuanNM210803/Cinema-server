package com.example.cinemaserver.service;

import com.example.cinemaserver.Request.SeatRequest;
import com.example.cinemaserver.model.Room;
import com.example.cinemaserver.model.Seat;
import com.example.cinemaserver.response.SeatResponse;

import java.sql.SQLException;
import java.util.List;

public interface ISeatService {
    List<Seat> findSeatsByRoomId(Long id);

    SeatResponse getSeatResponse(Seat seat) throws SQLException;

    Object getSeatBySeatId(Long id);

    void addNewSeat(SeatRequest seatRequest, Room room);

    Seat updateSeat(Long id, SeatRequest seatRequest);
}
