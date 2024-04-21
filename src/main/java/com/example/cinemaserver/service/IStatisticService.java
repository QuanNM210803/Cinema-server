package com.example.cinemaserver.service;

import com.example.cinemaserver.request.StatisticDateRequest;
import com.example.cinemaserver.response.BillResponse;
import com.example.cinemaserver.response.StatisticDateResponse;

import java.sql.SQLException;
import java.util.List;

public interface IStatisticService {
    List<StatisticDateResponse> getStatisticDate_MovieId_BranchId(StatisticDateRequest statisticDateRequest) throws SQLException;

    List<BillResponse> getStatisticMovieId_BranchId(StatisticDateRequest statisticDateRequest) throws SQLException;
}
