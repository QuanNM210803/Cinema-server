package com.example.cinemaserver.controller;

import com.example.cinemaserver.request.StatisticDateRequest;
import com.example.cinemaserver.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping("/Date_MovieId_BranchId")
    public ResponseEntity<?> getStatisticDates(@ModelAttribute StatisticDateRequest statisticDateRequest){
        try{
            if(statisticDateRequest.getStartDate()!=null && statisticDateRequest.getEndDate()!=null){
                return ResponseEntity.ok(statisticService.getStatisticDate_MovieId_BranchId(statisticDateRequest));
            }else if(statisticDateRequest.getStartDate()==null && statisticDateRequest.getEndDate()==null){
                if(statisticDateRequest.getMovieId()==null && statisticDateRequest.getBranchId()==null){
                    return ResponseEntity.ok("Select statistical objects");
                }
                return ResponseEntity.ok(statisticService.getStatisticMovieId_BranchId(statisticDateRequest));
            } else {
                return ResponseEntity.ok("Enter both start and end dates.");
            }
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }

}
