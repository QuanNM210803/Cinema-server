package com.example.cinemaserver.service;

import com.example.cinemaserver.model.Bill;
import com.example.cinemaserver.response.BillResponse;

import java.sql.SQLException;
import java.util.List;

public interface IBillService{

    List<Bill> getAllBills();

    BillResponse getBillResponse(Bill bill) throws SQLException;

    Bill getBill(Long id);

    List<Bill> getBillsByScheduleId(Long scheduleId);

    List<Bill> getBillsByUserId(Long userId);

    Bill addNewBill(Long userId);
}
