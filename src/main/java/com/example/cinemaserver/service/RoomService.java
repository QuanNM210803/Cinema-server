package com.example.cinemaserver.service;

import com.example.cinemaserver.model.Schedule;
import com.example.cinemaserver.repository.ScheduleRepository;
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
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.lang.module.FindException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class RoomService implements IRoomService{
    private final RoomRepository roomRepository;
    private final BranchService branchService;
    private final SeatRepository seatRepository;
    private final ScheduleRepository scheduleRepository;
    @Override
    public List<Room> getAllRoomsByBranchId(Long branchId) {
        branchService.getBranch(branchId);
        return roomRepository.findAllRoomsByBranchId(branchId);
    }

    @Override
    public Room getRoom(Long id) {
        return roomRepository.findById(id).orElseThrow(()->new FindException("Not found room."));
    }

    @Override
    public Room addNewRoom(RoomRequest roomRequest, Branch branch) throws IOException, SQLException {
        List<Room> rooms=roomRepository.findAllRoomsByBranchId(branch.getId());
        List<String> roomNames=rooms.stream().map(Room::getName).toList();
        if(roomNames.contains(roomRequest.getName())){
            throw new RuntimeException(roomRequest.getName()+" already exists.");
        }
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
            double price;
            if(seat_name.contains("A") || seat_name.contains("D")){
                price=50.;
            }
            else{
                price=60.;
            }
            seatRepository.save(new Seat(seat_name,price,thisRoom));
        }
        return thisRoom;
    }

    @Override
    public void deleteRoom(Long id) {
        this.getRoom(id);
        roomRepository.deleteById(id);
    }

    @Override
    public Room updateRoom(Long roomId, RoomRequest roomRequest) throws IOException, SQLException {
        Room room=this.getRoom(roomId);
        if(!room.getBranch().getStatus() && roomRequest.getStatus()){
            throw new RuntimeException("Cannot set inactive status because branch is inactive");
        }
        List<Room> rooms=roomRepository.findAllRoomsByBranchId(room.getBranch().getId());
        List<String> roomNames=rooms.stream().map(Room::getName).toList();
        if(roomNames.contains(roomRequest.getName()) && !roomRequest.getName().equals(room.getName())){
            throw new RuntimeException(roomRequest.getName() + " already exists.");
        }
        List<Schedule> futureSchedules=scheduleRepository.findSchedulesFutureByRoom(roomId, LocalDate.now(), LocalTime.now());
        if(roomRequest.getStatus() || futureSchedules.size() == 0){
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
        }else {
            throw new RuntimeException("Cannot set inactive status because showtime that haven't been broadcast yet exist.");
        }
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
    public RoomResponse getRoomResponseNonePhoto(Room room) {
        Branch branch=branchService.getBranch(room.getBranch().getId());
        BranchResponse branchResponse=branchService.getBranchResponseNonePhoto(branch);
        return new RoomResponse(room.getId()
                ,room.getName()
                ,room.getStatus()
                ,null
                ,branchResponse);
    }
}
