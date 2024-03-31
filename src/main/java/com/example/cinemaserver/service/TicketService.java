package com.example.cinemaserver.service;

import com.example.cinemaserver.model.Bill;
import com.example.cinemaserver.model.Schedule;
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

import java.sql.SQLException;
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
        try {
            return ticketRepository.findById(id).get();
        }catch (Exception e){
            throw new RuntimeException("Not found ticket.");
        }
    }

    @Override
    public List<Ticket> getTicketsByBillId(Long billId) {
        Bill bill=billService.getBill(billId);
        return ticketRepository.findByBillId(billId);
    }

    @Override
    public List<Ticket> getTicketsByScheduleId(Long scheduleId) {
        Schedule schedule=scheduleService.getScheduleById(scheduleId);
        return ticketRepository.findByScheduleId(scheduleId);
    }

    @Override
    public void addNewTickets(List<Long> seatScheduleIdList, Bill bill) throws Exception {
        List<Ticket> tickets=new ArrayList<>();
        for(Long seatScheduleId:seatScheduleIdList){
            Seat_Schedule seatSchedule=seatScheduleRepository.findById(seatScheduleId).get();
            if(!seatSchedule.getOrdered() && seatSchedule.getSchedule().getRoom().getStatus()
                && ( LocalDate.now().isBefore(seatSchedule.getSchedule().getStartDate()) ||
                (LocalDate.now().isEqual(seatSchedule.getSchedule().getStartDate()) && LocalTime.now().isBefore(seatSchedule.getSchedule().getStartTime())) )){
                Ticket ticket=new Ticket(seatSchedule.getPrice()
                        ,"https://toanhocbactrungnam.vn/uploads/news/2019_11/1573006985.png"
                        ,seatSchedule.getSeat()
                        ,bill
                        ,seatSchedule.getSchedule());
                tickets.add(ticket);
            }
        }
        if(tickets.size()>0){
            ticketRepository.saveAll(tickets);
            for(Ticket ticket:tickets){
                Seat_Schedule seatSchedule=seatScheduleRepository.findSeat_ScheduleByScheduleIdAndSeatId(ticket.getSchedule().getId(),ticket.getSeat().getId());
                seatSchedule.setOrdered(true);
                seatScheduleRepository.save(seatSchedule);
            }
        }else {
            throw new Exception("Booking error.");
        }
    }

    @Override
    public boolean checkBooking(List<Long> seatScheduleIdList) {
        for(Long seatScheduleId:seatScheduleIdList){
            Seat_Schedule seatSchedule=seatScheduleRepository.findById(seatScheduleId).get();
            if(!seatSchedule.getOrdered() && seatSchedule.getSchedule().getRoom().getStatus()
                && ( LocalDate.now().isBefore(seatSchedule.getSchedule().getStartDate()) ||
                    ( LocalDate.now().isEqual(seatSchedule.getSchedule().getStartDate()) && LocalTime.now().isBefore(seatSchedule.getSchedule().getStartTime()) ))){
                return true;
            }
        }
        return false;
    }

    @Override
    public TicketResponse getTicketResponse(Ticket ticket) throws SQLException {
        ScheduleResponse scheduleResponse=scheduleService.getScheduleResponse(ticket.getSchedule());
        SeatResponse seatResponse=seatService.getSeatResponse(ticket.getSeat());
        BillResponse billResponse=billService.getBillResponse(ticket.getBill());
        return new TicketResponse(ticket.getId(),ticket.getPrice(),ticket.getQr(),billResponse
                                    , seatResponse,scheduleResponse);
    }
}
