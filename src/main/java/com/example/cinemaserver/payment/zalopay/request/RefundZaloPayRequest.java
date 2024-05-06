package com.example.cinemaserver.payment.zalopay.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundZaloPayRequest {
    private Long userId;
    private String zp_trans_ip;
    private Long amount;
    private String description;
}
