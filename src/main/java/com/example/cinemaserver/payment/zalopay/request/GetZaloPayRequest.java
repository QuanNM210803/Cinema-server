package com.example.cinemaserver.payment.zalopay.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetZaloPayRequest {
    private Long userId;
    private String app_trans_id;
}
