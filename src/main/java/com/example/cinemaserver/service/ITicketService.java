package com.example.cinemaserver.service;

import com.example.cinemaserver.model.Bill;
import com.example.cinemaserver.model.Ticket;
import com.example.cinemaserver.response.TicketResponse;

import java.sql.SQLException;
import java.util.List;

public interface ITicketService {
    TicketResponse getTicketResponse(Ticket ticket) throws SQLException;

    Ticket getTicketById(Long id);

    List<Ticket> getTicketsByBillId(Long billId);

    List<Ticket> getTicketsByScheduleId(Long scheduleId);

    void addNewTickets(List<Long> seatScheduleIdList, Bill bill) throws Exception;

    boolean checkBooking(List<Long> seatScheduleId);
}
