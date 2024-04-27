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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.module.FindException;
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllRoomsByBranchId(@PathVariable("branchId") Long branchId) {
        try {
            List<Room> rooms=roomService.getAllRoomsByBranchId(branchId);
            List<RoomResponse> roomResponses=new ArrayList<>();
            for(Room room:rooms){
                roomResponses.add(roomService.getRoomResponseNonePhoto(room));
            }
            return ResponseEntity.ok(roomResponses);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{roomId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getRoom(@PathVariable("roomId") Long id){
        try{
            Room room=roomService.getRoom(id);
            RoomResponse roomResponse=roomService.getRoomResponse(room);
            return ResponseEntity.ok(roomResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PostMapping("/addNew/{branchId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addNewRoom(@PathVariable("branchId") Long branchId,
                                             @ModelAttribute RoomRequest roomRequest){
        try{
            Branch branch=branchService.getBranch(branchId);
            if(branch.getStatus()){
                Room room=roomService.addNewRoom(roomRequest,branch);
                RoomResponse roomResponse=roomService.getRoomResponse(room);
                return ResponseEntity.ok(roomResponse);
            }
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("This branch is inactive. Please open the branch's active status before adding a room.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //viet de day thoi, chu dung xoa room, thay vao do hay update status ve false
    @DeleteMapping("/delete/{roomId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteRoom(@PathVariable("roomId") Long id){
        try {
            roomService.deleteRoom(id);
            return ResponseEntity.ok("Delete successfully.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/update/{roomId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateRoom(@PathVariable("roomId") Long roomId,
                                                   @ModelAttribute RoomRequest roomRequest) throws SQLException, IOException {
        try{
            Room room=roomService.updateRoom(roomId,roomRequest);
            RoomResponse roomResponse=roomService.getRoomResponse(room);
            return ResponseEntity.ok(roomResponse);
        }catch (FindException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
