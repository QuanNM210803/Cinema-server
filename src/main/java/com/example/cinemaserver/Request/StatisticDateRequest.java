package com.example.cinemaserver.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticDateRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private Long movieId;
    private Long branchId;
}
