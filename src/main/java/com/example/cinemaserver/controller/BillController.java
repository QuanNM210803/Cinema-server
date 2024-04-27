package com.example.cinemaserver.controller;

import com.example.cinemaserver.model.Bill;
import com.example.cinemaserver.response.BillResponse;
import com.example.cinemaserver.service.BillService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/bills")
@AllArgsConstructor
public class BillController {
    private final BillService billService;
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<BillResponse>> getAllBills() {
        List<Bill> bills=billService.getAllBills();
        List<BillResponse> billResponses=new ArrayList<>();
        for(Bill bill:bills){
            billResponses.add(billService.getBillResponse(bill));
        }
        return ResponseEntity.ok(billResponses);
    }

    @GetMapping("/{billId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') " +
            "and @billService.getBillResponse(@billService.getBill(#id)).userResponse.email==principal.username)")
    public ResponseEntity<?> getBill(@PathVariable("billId") Long id){
        try{
            Bill bill=billService.getBill(id);
            BillResponse billResponse=billService.getBillResponse(bill);
            return ResponseEntity.ok(billResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/all/schedule/{scheduleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getBillsByScheduleId(@PathVariable("scheduleId") Long scheduleId){
        try{
            List<Bill> bills=billService.getBillsByScheduleId(scheduleId);
            List<BillResponse> billResponses=new ArrayList<>();
            for(Bill bill:bills){
                billResponses.add(billService.getBillResponse(bill));
            }
            return ResponseEntity.ok(billResponses);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/all/user/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') " +
            "and @userService.getUserById(#userId).email==principal.username)")
    public ResponseEntity<?> getBillsByUserId(@PathVariable("userId") Long userId){
        try{
            List<Bill> bills=billService.getBillsByUserId(userId);
            List<BillResponse> billResponses=new ArrayList<>();
            for(Bill bill:bills){
                billResponses.add(billService.getBillResponse(bill));
            }
            return ResponseEntity.ok(billResponses);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
