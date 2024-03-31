package com.example.cinemaserver.controller;

import com.example.cinemaserver.Request.SeatRequest;
import com.example.cinemaserver.model.Room;
import com.example.cinemaserver.model.Seat;
import com.example.cinemaserver.response.SeatResponse;
import com.example.cinemaserver.service.RoomService;
import com.example.cinemaserver.service.SeatService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/seats")
@AllArgsConstructor
public class SeatController {
    private final SeatService seatService;
    private final RoomService roomService;
    @GetMapping("/all/{roomId}")
    public ResponseEntity<?> getSeatsByRoomId(@PathVariable("roomId") Long id) throws SQLException {
        List<Seat> seats=seatService.findSeatsByRoomId(id);
        List<SeatResponse> seatResponses=new ArrayList<>();
        for(Seat seat:seats){
            seatResponses.add(seatService.getSeatResponse(seat));
        }
        return ResponseEntity.ok(seatResponses);
    }

    @GetMapping("/{seatId}")
    public ResponseEntity<?> getSeatBySeatId(@PathVariable("seatId") Long id){
        return ResponseEntity.ok(seatService.getSeatBySeatId(id));
    }

    @PostMapping("/addNew/{roomId}")
    public ResponseEntity<String> addNewSeat(@PathVariable("roomId") Long roomId
                                            , @ModelAttribute SeatRequest seatRequest){
        try{
            Room room=roomService.getRoom(roomId);
            seatService.addNewSeat(seatRequest,room);
            return ResponseEntity.ok("Add seat successfully.");
        }catch (Exception e){
            return ResponseEntity.ok("Error add seat.");
        }
    }

    @PutMapping("/update/{seatId}")
    public ResponseEntity<?> updateSeat(@PathVariable("seatId") Long id
                                        ,@ModelAttribute SeatRequest seatRequest) throws SQLException {
        try{
            Seat seat=seatService.updateSeat(id,seatRequest);
            SeatResponse seatResponse=seatService.getSeatResponse(seat);
            return ResponseEntity.ok(seatResponse);
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }

    }
}
