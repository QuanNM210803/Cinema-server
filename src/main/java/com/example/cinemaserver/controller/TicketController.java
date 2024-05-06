package com.example.cinemaserver.controller;

import com.example.cinemaserver.request.BookingTicketRequest;
import com.example.cinemaserver.model.Bill;
import com.example.cinemaserver.model.Ticket;
import com.example.cinemaserver.response.TicketResponse;
import com.example.cinemaserver.service.BillService;
import com.example.cinemaserver.service.TicketService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') " +
            "and @ticketService.getTicketResponse(@ticketService.getTicketById(#id)).billResponse.userResponse.email==principal.username)")
    public ResponseEntity<?> getTicketById(@PathVariable("ticketId") Long id){
        try{
            Ticket ticket=ticketService.getTicketById(id);
            TicketResponse ticketResponse=ticketService.getTicketResponse(ticket);
            return ResponseEntity.ok(ticketResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/all/bill/{billId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') " +
            "and @billService.getBillResponse(@billService.getBill(#billId)).userResponse.email==principal.username)")
    public ResponseEntity<?> getTicketsByBillId(@PathVariable("billId") Long billId){
        try{
            List<Ticket> tickets=ticketService.getTicketsByBillId(billId);
            List<TicketResponse> ticketResponses=new ArrayList<>();
            for(Ticket ticket:tickets){
                ticketResponses.add(ticketService.getTicketResponse(ticket));
            }
            return ResponseEntity.ok(ticketResponses);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/all/schedule/{scheduleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getTicketsByScheduleId(@PathVariable("scheduleId") Long scheduleId){
        try{
            List<Ticket> tickets=ticketService.getTicketsByScheduleId(scheduleId);
            List<TicketResponse> ticketResponses=new ArrayList<>();
            for(Ticket ticket:tickets){
                ticketResponses.add(ticketService.getTicketResponse(ticket));
            }
            return ResponseEntity.ok(ticketResponses);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/addNew")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> addNewTicket(@ModelAttribute BookingTicketRequest bookingTicketRequest){
        try{
            if(bookingTicketRequest.getSeatScheduleId().size()>0){
                List<Long> seatScheduleIdList=bookingTicketRequest.getSeatScheduleId();
                if(ticketService.checkBooking(seatScheduleIdList)){
                    Bill bill=billService.addNewBill(bookingTicketRequest.getUserId());
                    List<Ticket> tickets=ticketService.addNewTickets(seatScheduleIdList,bill);
                    List<TicketResponse> ticketResponses=new ArrayList<>();
                    for(Ticket ticket:tickets){
                        ticketResponses.add(ticketService.getTicketResponse(ticket));
                    }
                    return ResponseEntity.ok(ticketResponses);
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Choose at least 1 seat to book a ticket.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/checkBookingTicket")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> checkBookingTicket(@RequestParam("seatScheduleId") List<Long> seatScheduleId){
        try{
            return ResponseEntity.ok(ticketService.checkBooking(seatScheduleId));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
