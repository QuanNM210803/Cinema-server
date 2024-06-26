package com.example.cinemaserver.service;

import com.example.cinemaserver.request.RoomRequest;
import com.example.cinemaserver.model.Branch;
import com.example.cinemaserver.model.Room;
import com.example.cinemaserver.response.RoomResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IRoomService {
    List<Room> getAllRoomsByBranchId(Long branchId);

    String getRoomPhoto(Room room) throws SQLException;

    RoomResponse getRoomResponse(Room room) throws SQLException;

    Room getRoom(Long id);

    Room addNewRoom(RoomRequest roomRequest, Branch branch) throws IOException, SQLException;

    void deleteRoom(Long id);

    Room updateRoom(Long roomId, RoomRequest roomRequest) throws IOException, SQLException;


    RoomResponse getRoomResponseNonePhoto(Room room) throws SQLException;
}
