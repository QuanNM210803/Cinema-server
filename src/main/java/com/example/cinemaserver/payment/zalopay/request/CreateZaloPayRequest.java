package com.example.cinemaserver.payment.zalopay.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateZaloPayRequest {
    private Long userId;
    private Long amount;
    private List<Long> seatScheduleId;
}
