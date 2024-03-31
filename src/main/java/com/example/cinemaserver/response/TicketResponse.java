package com.example.cinemaserver.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketResponse {
    private Long id;
    private Double price;
    private String qr;
    private BillResponse billResponse;
    private SeatResponse seatResponse;
    private ScheduleResponse scheduleResponse;
}
