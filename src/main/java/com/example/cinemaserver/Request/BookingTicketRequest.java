package com.example.cinemaserver.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
//phuc vu cho viec dat ve, khach co the dat nhieu ve
public class BookingTicketRequest {
    private Long userId;
    private List<Long> seatScheduleId;
}
