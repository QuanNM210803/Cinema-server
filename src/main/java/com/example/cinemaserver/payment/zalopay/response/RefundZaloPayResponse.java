package com.example.cinemaserver.payment.zalopay.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundZaloPayResponse {
    @JsonProperty("return_code")
    private String return_code;

    @JsonProperty("return_message")
    private String return_message;

    @JsonProperty("sub_return_code")
    private String sub_return_code;

    @JsonProperty("sub_return_message")
    private String sub_return_message;

    @JsonProperty("refund_id")
    private String refund_id;
}
