package com.example.cinemaserver.controller;

import com.example.cinemaserver.request.SeatRequest;
import com.example.cinemaserver.model.Room;
import com.example.cinemaserver.model.Seat;
import com.example.cinemaserver.response.SeatResponse;
import com.example.cinemaserver.service.RoomService;
import com.example.cinemaserver.service.SeatService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/seats")
@AllArgsConstructor
public class SeatController {
    private final SeatService seatService;
    private final RoomService roomService;
    @GetMapping("/all/{roomId}")
    public ResponseEntity<?> getSeatsByRoomId(@PathVariable("roomId") Long id) {
        try {
            List<Seat> seats=seatService.findSeatsByRoomId(id);
            List<SeatResponse> seatResponses=new ArrayList<>();
            for(Seat seat:seats){
                seatResponses.add(seatService.getSeatResponse(seat));
            }
            return ResponseEntity.ok(seatResponses);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{seatId}")
    public ResponseEntity<?> getSeatBySeatId(@PathVariable("seatId") Long id){
        try {
            Seat seat=seatService.getSeatBySeatId(id);
            SeatResponse seatResponse=seatService.getSeatResponse(seat);
            return ResponseEntity.ok(seatResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/addNew/{roomId}")
    public ResponseEntity<?> addNewSeat(@PathVariable("roomId") Long roomId
                                            , @ModelAttribute SeatRequest seatRequest){
        try{
            Room room=roomService.getRoom(roomId);
            Seat seat=seatService.addNewSeat(seatRequest,room);
            SeatResponse seatResponse=seatService.getSeatResponse(seat);
            return ResponseEntity.ok(seatResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/update/{seatId}")
    public ResponseEntity<?> updateSeat(@PathVariable("seatId") Long id
                                        ,@ModelAttribute SeatRequest seatRequest) {
        try{
            Seat seat=seatService.updateSeat(id,seatRequest);
            SeatResponse seatResponse=seatService.getSeatResponse(seat);
            return ResponseEntity.ok(seatResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
