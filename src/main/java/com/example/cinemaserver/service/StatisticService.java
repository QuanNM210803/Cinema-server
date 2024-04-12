package com.example.cinemaserver.service;

import com.example.cinemaserver.Request.StatisticDateRequest;
import com.example.cinemaserver.model.Bill;
import com.example.cinemaserver.repository.BillRepository;
import com.example.cinemaserver.response.BillResponse;
import com.example.cinemaserver.response.StatisticDateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticService implements IStatisticService{
    private final BillRepository billRepository;
    private final BillService billService;
    private final TicketService ticketService;
    @Override
    public Object getStatisticDate_MovieId_BranchId(StatisticDateRequest statisticDateRequest) throws SQLException {
        LocalDate startDate=statisticDateRequest.getStartDate();
        LocalDate endDate=statisticDateRequest.getEndDate();
        Long movieId=statisticDateRequest.getMovieId();
        Long branchId=statisticDateRequest.getBranchId();

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
    public Object getStatisticMovieId_BranchId(StatisticDateRequest statisticDateRequest) throws SQLException {
        Long movieId=statisticDateRequest.getMovieId();
        Long branchId=statisticDateRequest.getBranchId();

        List<Bill> bills=billRepository.findBillsByMovieId_BranchId(movieId,branchId);
        List<BillResponse> billResponses=new ArrayList<>();
        for(Bill bill:bills){
            billResponses.add(billService.getBillResponse(bill));
        }
        return billResponses;
    }
}
