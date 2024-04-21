package com.example.cinemaserver.controller;

import com.example.cinemaserver.request.StatisticDateRequest;
import com.example.cinemaserver.response.BillResponse;
import com.example.cinemaserver.response.StatisticDateResponse;
import com.example.cinemaserver.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;

    @PostMapping("/Date_MovieId_BranchId")
    public ResponseEntity<?> getStatisticDates(@ModelAttribute StatisticDateRequest statisticDateRequest){
        try{
            if(statisticDateRequest.getStartDate()!=null && statisticDateRequest.getEndDate()!=null){
                List<StatisticDateResponse> statisticDateResponses=statisticService.getStatisticDate_MovieId_BranchId(statisticDateRequest);
                return ResponseEntity.ok(statisticDateResponses);
            }else if(statisticDateRequest.getStartDate()==null && statisticDateRequest.getEndDate()==null){
                if(statisticDateRequest.getMovieId()==null && statisticDateRequest.getBranchId()==null){
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Enter statistics object.");
                }
                List<BillResponse> billResponses=statisticService.getStatisticMovieId_BranchId(statisticDateRequest);
                return ResponseEntity.ok(billResponses);
            } else {
                return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("Enter both start and end dates.");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
