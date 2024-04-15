package com.example.cinemaserver.service;

import com.example.cinemaserver.exception.ResourceNotFoundException;
import com.example.cinemaserver.request.RoomRequest;
import com.example.cinemaserver.model.Branch;
import com.example.cinemaserver.model.Room;
import com.example.cinemaserver.model.Seat;
import com.example.cinemaserver.repository.RoomRepository;
import com.example.cinemaserver.repository.SeatRepository;
import com.example.cinemaserver.response.BranchResponse;
import com.example.cinemaserver.response.RoomResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class RoomService implements IRoomService{
    private final RoomRepository roomRepository;
    private final BranchService branchService;
    private final SeatRepository seatRepository;
    @Override
    public List<Room> getAllRoomsByBranchId(Long branchId) {
        return roomRepository.findAllRoomsByBranchId(branchId);
    }

    @Override
    public Room getRoom(Long id) {
        return roomRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Room not found."));
    }

    @Override
    public void addNewRoom(RoomRequest roomRequest, Branch branch) throws IOException, SQLException {
        Blob blob=null;
        if(!roomRequest.getPhoto().isEmpty() && roomRequest.getPhoto()!=null){
            byte[] bytes=roomRequest.getPhoto().getBytes();
            blob=new SerialBlob(bytes);
        }
        Room room=new Room(roomRequest.getName(),blob,branch);
        Room thisRoom=roomRepository.save(room);

        List<String> seat_names=new ArrayList<>(Arrays.asList("A1","A2","A3","A4","A5","A6"
                                                            ,"B1","B2","B3","B4","B5","B6"
                                                            ,"C1","C2","C3","C4","C5","C6"
                                                            ,"D1","D2","D3","D4","D5","D6"));
        for(String seat_name:seat_names){
            double price=0.;
            if(seat_name.contains("A") || seat_name.contains("D")){
                price=50.;
            }
            else{
                price=60.;
            }
            seatRepository.save(new Seat(seat_name,price,thisRoom));
        }

    }

    @Override
    public ResponseEntity<String> deleteRoom(Long id) {
        try{
            Room room=roomRepository.findById(id).get();
            roomRepository.deleteById(id);
            return  ResponseEntity.ok("Delete successfully.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching room.");
        }
    }

    @Override
    public Room updateRoom(Long roomId, RoomRequest roomRequest) throws IOException, SQLException {
        Room room=roomRepository.findById(roomId)
                .orElseThrow(()->new ResourceNotFoundException("Room not found"));
        if(!StringUtils.isBlank(roomRequest.getName())){
            room.setName(roomRequest.getName());
        }
        if(!roomRequest.getPhoto().isEmpty() && roomRequest.getPhoto()!=null){
            byte[] bytes=roomRequest.getPhoto().getBytes();
            Blob blob=new SerialBlob(bytes);
            room.setPhoto(blob);
        }
        if(room.getBranch().getStatus()){
            room.setStatus(roomRequest.getStatus());
        }
        return roomRepository.save(room);
    }


    @Override
    public String getRoomPhoto(Room room) throws SQLException {
        Blob blob=room.getPhoto();
        byte[] photoBytes = blob!=null ? blob.getBytes(1,(int)blob.length()):null;
        String base64photo = photoBytes!=null && photoBytes.length>0 ?  Base64.encodeBase64String(photoBytes):"";
        return base64photo;
    }
    @Override
    public RoomResponse getRoomResponse(Room room) throws SQLException {
        Branch branch=branchService.getBranch(room.getBranch().getId());
        BranchResponse branchResponse=branchService.getBranchResponseNonePhoto(branch);
        return new RoomResponse(room.getId()
                ,room.getName()
                ,room.getStatus()
                ,getRoomPhoto(room)
                ,branchResponse);
    }

    @Override
    public RoomResponse getRoomResponseNonePhoto(Room room) throws SQLException {
        Branch branch=branchService.getBranch(room.getBranch().getId());
        BranchResponse branchResponse=branchService.getBranchResponseNonePhoto(branch);
        return new RoomResponse(room.getId()
                ,room.getName()
                ,room.getStatus()
                ,null
                ,branchResponse);
    }
}
