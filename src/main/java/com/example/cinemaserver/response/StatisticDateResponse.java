package com.example.cinemaserver.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StatisticDateResponse {
    private String date;
    private Long numberOfTickets;
    private Double revenue;
    private List<BillResponse> billResponses;
    public StatisticDateResponse(String date, List<BillResponse> billResponses) {
        this.date = date;
        this.billResponses = billResponses;
        this.numberOfTickets = 0L;
        this.revenue=0.;
        for(BillResponse billResponse:billResponses){
            this.numberOfTickets+=billResponse.getNumberOfTickets();
            this.revenue+=billResponse.getPayment();
        }
    }
}
