package com.example.cinemaserver.service;

import com.example.cinemaserver.model.Bill;
import com.example.cinemaserver.model.Seat_Schedule;
import com.example.cinemaserver.model.Ticket;
import com.example.cinemaserver.repository.Seat_ScheduleRepository;
import com.example.cinemaserver.repository.TicketRepository;
import com.example.cinemaserver.response.BillResponse;
import com.example.cinemaserver.response.ScheduleResponse;
import com.example.cinemaserver.response.SeatResponse;
import com.example.cinemaserver.response.TicketResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.module.FindException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TicketService implements ITicketService{
    private final TicketRepository ticketRepository;
    private final SeatService seatService;
    private final BillService billService;
    private final ScheduleService scheduleService;
    private final Seat_ScheduleRepository seatScheduleRepository;

    @Override
    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id).orElseThrow(()->new FindException("Not found ticket."));
    }

    @Override
    public List<Ticket> getTicketsByBillId(Long billId) {
        billService.getBill(billId);
        return ticketRepository.findByBillId(billId);
    }

    @Override
    public List<Ticket> getTicketsByScheduleId(Long scheduleId) {
        scheduleService.getScheduleById(scheduleId);
        return ticketRepository.findByScheduleId(scheduleId);
    }

    @Override
    public List<Ticket> addNewTickets(List<Long> seatScheduleIdList, Bill bill) {
        List<Ticket> tickets=new ArrayList<>();
        for(Long seatScheduleId:seatScheduleIdList){
            Seat_Schedule seatSchedule=seatScheduleRepository.findById(seatScheduleId).orElseThrow(()->new FindException("Not found seat to reserve."));
            Ticket ticket=new Ticket(seatSchedule.getPrice()
                    ,"https://toanhocbactrungnam.vn/uploads/news/2019_11/1573006985.png"
                    ,seatSchedule.getSeat()
                    ,bill
                    ,seatSchedule.getSchedule());
            tickets.add(ticket);
        }
        List<Ticket> theTickets=ticketRepository.saveAll(tickets);
        for(Ticket ticket:tickets){
            Seat_Schedule seatSchedule=seatScheduleRepository.findSeat_ScheduleByScheduleIdAndSeatId(ticket.getSchedule().getId(),ticket.getSeat().getId());
            seatSchedule.setOrdered(true);
            seatScheduleRepository.save(seatSchedule);
        }
        return theTickets;
    }

    @Override
    public boolean checkBooking(List<Long> seatScheduleIdList) {
        for(Long seatScheduleId:seatScheduleIdList){
            Seat_Schedule seatSchedule=seatScheduleRepository.findById(seatScheduleId).orElseThrow(()->new FindException("Not found seat to reserve."));
            if(seatSchedule.getOrdered()){
                throw new RuntimeException("The seat has been booked.");
            }
            if(!seatSchedule.getSchedule().getRoom().getStatus()){
                throw new RuntimeException("Can't book tickets because screening room is not operating.");
            }
            if( !( LocalDate.now().isBefore(seatSchedule.getSchedule().getStartDate()) ||
                    ( LocalDate.now().isEqual(seatSchedule.getSchedule().getStartDate()) && LocalTime.now().isBefore(seatSchedule.getSchedule().getStartTime())))){
                throw new RuntimeException("Can't book tickets because schedule has been shown.");
            }
        }
        return true;
    }

    @Override
    public TicketResponse getTicketResponse(Ticket ticket) {
        ScheduleResponse scheduleResponse=scheduleService.getScheduleResponse(ticket.getSchedule());
        SeatResponse seatResponse=seatService.getSeatResponse(ticket.getSeat());
        BillResponse billResponse=billService.getBillResponse(ticket.getBill());
        return new TicketResponse(ticket.getId(),ticket.getPrice(),ticket.getQr(),billResponse
                                    , seatResponse,scheduleResponse);
    }
}
