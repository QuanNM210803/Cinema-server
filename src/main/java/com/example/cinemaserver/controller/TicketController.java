package com.example.cinemaserver.controller;

import com.example.cinemaserver.request.BookingTicketRequest;
import com.example.cinemaserver.model.Bill;
import com.example.cinemaserver.model.Ticket;
import com.example.cinemaserver.response.TicketResponse;
import com.example.cinemaserver.service.BillService;
import com.example.cinemaserver.service.TicketService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/tickets")
@AllArgsConstructor
public class TicketController {
    private final TicketService ticketService;
    private final BillService billService;
    @GetMapping("/{ticketId}")
    public ResponseEntity<?> getTicketById(@PathVariable("ticketId") Long id){
        try{
            Ticket ticket=ticketService.getTicketById(id);
            TicketResponse ticketResponse=ticketService.getTicketResponse(ticket);
            return ResponseEntity.ok(ticketResponse);
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/all/bill/{billId}")
    public ResponseEntity<?> getTicketsByBillId(@PathVariable("billId") Long billId){
        try{
            List<Ticket> tickets=ticketService.getTicketsByBillId(billId);
            List<TicketResponse> ticketResponses=new ArrayList<>();
            for(Ticket ticket:tickets){
                ticketResponses.add(ticketService.getTicketResponse(ticket));
            }
            return ResponseEntity.ok(ticketResponses);
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/all/schedule/{scheduleId}")
    public ResponseEntity<?> getTicketsByScheduleId(@PathVariable("scheduleId") Long scheduleId){
        try{
            List<Ticket> tickets=ticketService.getTicketsByScheduleId(scheduleId);
            List<TicketResponse> ticketResponses=new ArrayList<>();
            for(Ticket ticket:tickets){
                ticketResponses.add(ticketService.getTicketResponse(ticket));
            }
            return ResponseEntity.ok(ticketResponses);
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @PostMapping("/addNew")
    public ResponseEntity<?> addNewTicket(@ModelAttribute BookingTicketRequest bookingTicketRequest){
        try{
            if(bookingTicketRequest.getSeatScheduleId().size()>0){
                List<Long> seatScheduleIdList=bookingTicketRequest.getSeatScheduleId();
                if(ticketService.checkBooking(bookingTicketRequest.getSeatScheduleId())){
                    Bill bill=billService.addNewBill(bookingTicketRequest.getUserId());
                    ticketService.addNewTickets(seatScheduleIdList,bill);
                    return ResponseEntity.ok("Booking successfully.");
                }else {
                    return ResponseEntity.ok("Booking error.");
                }
            }
            return ResponseEntity.ok("Choose at least 1 seat to book a ticket");
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }
}
