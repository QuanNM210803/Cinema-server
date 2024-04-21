package com.example.cinemaserver.service;

import com.example.cinemaserver.request.StatisticDateRequest;
import com.example.cinemaserver.model.Bill;
import com.example.cinemaserver.repository.BillRepository;
import com.example.cinemaserver.response.BillResponse;
import com.example.cinemaserver.response.StatisticDateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticService implements IStatisticService{
    private final BillRepository billRepository;
    private final BillService billService;
    private final MovieService movieService;
    private final BranchService branchService;
    @Override
    public List<StatisticDateResponse> getStatisticDate_MovieId_BranchId(StatisticDateRequest statisticDateRequest) {
        LocalDate startDate=statisticDateRequest.getStartDate();
        LocalDate endDate=statisticDateRequest.getEndDate();
        Long movieId=statisticDateRequest.getMovieId();
        Long branchId=statisticDateRequest.getBranchId();

        if(branchId!=null){
            branchService.getBranch(branchId);
        }
        if(movieId!=null){
            movieService.getMovie(movieId);
        }

        List<StatisticDateResponse> statisticDateResponses=new ArrayList<>();
        for(LocalDate currentDate=startDate;currentDate.isBefore(endDate.plusDays(1));currentDate=currentDate.plusDays(1)){
            List<Bill> bills=billRepository.findBillsByDate_MovieId_BranchId(currentDate,movieId,branchId);
            List<BillResponse> billResponses=new ArrayList<>();
            for(Bill bill:bills){
                billResponses.add(billService.getBillResponse(bill));
            }
            statisticDateResponses.add(new StatisticDateResponse(
                    currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    ,billResponses));
        }
        return statisticDateResponses;
    }

    @Override
    public List<BillResponse> getStatisticMovieId_BranchId(StatisticDateRequest statisticDateRequest) {
        Long movieId=statisticDateRequest.getMovieId();
        Long branchId=statisticDateRequest.getBranchId();

        if(branchId!=null){
            branchService.getBranch(branchId);
        }
        if(movieId!=null){
            movieService.getMovie(movieId);
        }

        List<Bill> bills=billRepository.findBillsByMovieId_BranchId(movieId,branchId);
        List<BillResponse> billResponses=new ArrayList<>();
        for(Bill bill:bills){
            billResponses.add(billService.getBillResponse(bill));
        }
        return billResponses;
    }
}
