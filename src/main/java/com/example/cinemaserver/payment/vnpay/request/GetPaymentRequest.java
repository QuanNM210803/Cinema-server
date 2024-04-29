package com.example.cinemaserver.payment.vnpay.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPaymentRequest {
    private Long userId;
    private String vnp_TxnRef;
    private String vnp_TransactionDate;
}
