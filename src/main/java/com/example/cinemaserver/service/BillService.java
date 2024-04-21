package com.example.cinemaserver.service;

import com.example.cinemaserver.model.Bill;
import com.example.cinemaserver.model.Ticket;
import com.example.cinemaserver.model.User;
import com.example.cinemaserver.repository.BillRepository;
import com.example.cinemaserver.repository.TicketRepository;
import com.example.cinemaserver.response.BillResponse;
import com.example.cinemaserver.response.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.module.FindException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class BillService implements IBillService{
    private final BillRepository billRepository;
    private final UserService userService;
    private final ScheduleService scheduleService;
    private final TicketRepository ticketRepository;
    @Override
    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    @Override
    public Bill getBill(Long id) {
        return billRepository.findById(id).orElseThrow(()->new FindException("Not found bill."));
    }

    @Override
    public List<Bill> getBillsByScheduleId(Long scheduleId) {
        scheduleService.getScheduleById(scheduleId);
        return billRepository.findByScheduleId(scheduleId);
    }

    @Override
    public List<Bill> getBillsByUserId(Long userId) {
        userService.getUserById(userId);
        return billRepository.findByUserId(userId);
    }

    @Override
    public Bill addNewBill(Long userId) {
        Bill bill=new Bill(LocalDateTime.now(),userService.getUserById(userId));
        return billRepository.save(bill);
    }

    @Override
    public BillResponse getBillResponse(Bill bill) {
        User user=bill.getUser();
        UserResponse userResponse=userService.getUserResponseNonePhoto(user);
        DateTimeFormatter formatDate= DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatTime= DateTimeFormatter.ofPattern("HH:mm:ss");
        List<Ticket> tickets=ticketRepository.findByBillId(bill.getId());
        return new BillResponse( bill.getId()
                                , bill.getCreatedTime().format(formatDate)
                                , bill.getCreatedTime().format(formatTime)
                                , tickets.stream().mapToDouble(Ticket::getPrice).sum()
                                , (long) tickets.size()
                                , userResponse);
    }
}
