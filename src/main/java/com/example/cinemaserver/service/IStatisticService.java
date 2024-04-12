package com.example.cinemaserver.service;

import com.example.cinemaserver.Request.StatisticDateRequest;

import java.sql.SQLException;

public interface IStatisticService {
    Object getStatisticDate_MovieId_BranchId(StatisticDateRequest statisticDateRequest) throws SQLException;

    Object getStatisticMovieId_BranchId(StatisticDateRequest statisticDateRequest) throws SQLException;
}
