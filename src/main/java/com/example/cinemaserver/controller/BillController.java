package com.example.cinemaserver.controller;

import com.example.cinemaserver.model.Bill;
import com.example.cinemaserver.response.BillResponse;
import com.example.cinemaserver.service.BillService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/bills")
@AllArgsConstructor
public class BillController {
    private final BillService billService;
    @GetMapping("/all")
    public ResponseEntity<List<BillResponse>> getAllBills() throws SQLException {
        List<Bill> bills=billService.getAllBills();
        List<BillResponse> billResponses=new ArrayList<>();
        for(Bill bill:bills){
            billResponses.add(billService.getBillResponse(bill));
        }
        return ResponseEntity.ok(billResponses);
    }
    //test thu cai git
    @GetMapping("/{billId}")
    public ResponseEntity<?> getBill(@PathVariable("billId") Long id){
        try{
            Bill bill=billService.getBill(id);
            BillResponse billResponse=billService.getBillResponse(bill);
            return ResponseEntity.ok(billResponse);
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/all/schedule/{scheduleId}")
    public ResponseEntity<?> getBillsByScheduleId(@PathVariable("scheduleId") Long scheduleId) throws SQLException{
        try{
            List<Bill> bills=billService.getBillsByScheduleId(scheduleId);
            List<BillResponse> billResponses=new ArrayList<>();
            for(Bill bill:bills){
                billResponses.add(billService.getBillResponse(bill));
            }
            return ResponseEntity.ok(billResponses);
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/all/user/{userId}")
    public ResponseEntity<?> getBillsByUserId(@PathVariable("userId") Long userId) throws SQLException{
        try{
            List<Bill> bills=billService.getBillsByUserId(userId);
            List<BillResponse> billResponses=new ArrayList<>();
            for(Bill bill:bills){
                billResponses.add(billService.getBillResponse(bill));
            }
            return ResponseEntity.ok(billResponses);
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }
}
