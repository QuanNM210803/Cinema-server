package com.example.cinemaserver.payment.vnpay.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVNPayRequest {
    private Long userId;
    private Long amount;
    private List<Long> seatScheduleId;
}
