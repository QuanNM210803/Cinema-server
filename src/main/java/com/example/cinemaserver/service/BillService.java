package com.example.cinemaserver.service;

import com.example.cinemaserver.model.Bill;
import com.example.cinemaserver.model.Schedule;
import com.example.cinemaserver.model.Ticket;
import com.example.cinemaserver.model.User;
import com.example.cinemaserver.repository.BillRepository;
import com.example.cinemaserver.repository.TicketRepository;
import com.example.cinemaserver.response.BillResponse;
import com.example.cinemaserver.response.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
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
        try {
            return billRepository.findById(id).get();
        }catch (Exception e){
            throw new RuntimeException("Not found bill.");
        }
    }

    @Override
    public List<Bill> getBillsByScheduleId(Long scheduleId) {
        Schedule schedule=scheduleService.getScheduleById(scheduleId);
        return billRepository.findByScheduleId(scheduleId);
    }

    @Override
    public List<Bill> getBillsByUserId(Long userId) {
        User user=userService.getUserById(userId);
        return billRepository.findByUserId(userId);
    }

    @Override
    public Bill addNewBill(Long userId) {
        Bill bill=new Bill(LocalDateTime.now(),userService.getUserById(userId));
        return billRepository.save(bill);
    }

    @Override
    public BillResponse getBillResponse(Bill bill) throws SQLException {
        User user=bill.getUser();
        UserResponse userResponse=userService.getUserResponse(user);
        System.out.println(bill.getCreatedTime());
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
