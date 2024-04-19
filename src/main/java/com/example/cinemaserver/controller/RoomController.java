package com.example.cinemaserver.controller;

import com.example.cinemaserver.request.RoomRequest;
import com.example.cinemaserver.model.Branch;
import com.example.cinemaserver.model.Room;
import com.example.cinemaserver.response.RoomResponse;
import com.example.cinemaserver.service.BranchService;
import com.example.cinemaserver.service.RoomService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rooms")
@AllArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private final BranchService branchService;
    @GetMapping("/all/{branchId}")
    public ResponseEntity<List<RoomResponse>> getAllRoomsByBranchId(@PathVariable("branchId") Long branchId) throws SQLException {
        List<Room> rooms=roomService.getAllRoomsByBranchId(branchId);
        List<RoomResponse> roomResponses=new ArrayList<>();
        for(Room room:rooms){
            roomResponses.add(roomService.getRoomResponseNonePhoto(room));
        }
        return ResponseEntity.ok(roomResponses);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoom(@PathVariable("roomId") Long id){
        try{
            Room room=roomService.getRoom(id);
            RoomResponse roomResponse=roomService.getRoomResponse(room);
            return ResponseEntity.ok(roomResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching Room.");
        }
    }
    @PostMapping("/addNew/{branchId}")
    public ResponseEntity<String> addNewRoom(@PathVariable("branchId") Long branchId,
                                             @ModelAttribute RoomRequest roomRequest){
        try{
            Branch branch=branchService.getBranch(branchId);
            if(branch.getStatus()){
                Room room=roomService.addNewRoom(roomRequest,branch);
                RoomResponse roomResponse=roomService.getRoomResponse(room);
                return ResponseEntity.ok("Add room successfully.");
            }
            return ResponseEntity.ok("This room is inactive. Please open the branch's active status before adding a room");
        }catch (Exception e){
            return ResponseEntity.ok("Error");
        }
    }

    //viet de day thoi, chu dung xoa room, thay vao do hay update status ve false
    @DeleteMapping("/delete/{roomId}")
    public ResponseEntity<String> deleteRoom(@PathVariable("roomId") Long id){
        return roomService.deleteRoom(id);
    }

    @PutMapping("/update/{roomId}")
    public ResponseEntity<?> updateRoom(@PathVariable("roomId") Long roomId,
                                                   @ModelAttribute RoomRequest roomRequest) throws SQLException, IOException {
        try{
            Room room=roomService.updateRoom(roomId,roomRequest);
            RoomResponse roomResponse=roomService.getRoomResponse(room);
            return ResponseEntity.ok(roomResponse);
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }
}
