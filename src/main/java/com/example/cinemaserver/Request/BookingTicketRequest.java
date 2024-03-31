package com.example.cinemaserver.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingTicketRequest {
    private Long userId;
    private List<Long> seatScheduleId;
}
