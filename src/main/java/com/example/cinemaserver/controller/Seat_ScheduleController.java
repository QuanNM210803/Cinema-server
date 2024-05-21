package com.example.cinemaserver.controller;

import com.example.cinemaserver.model.Seat_Schedule;
import com.example.cinemaserver.response.Seat_ScheduleResponse;
import com.example.cinemaserver.service.Seat_ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/seat_schedule")
@AllArgsConstructor
public class Seat_ScheduleController {
    private final Seat_ScheduleService seatScheduleService;
    @GetMapping("/get/{scheduleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getSeat_ScheduleByScheduleId(@PathVariable("scheduleId") Long scheduleId){
        try{
            List<Seat_Schedule> seatSchedules= seatScheduleService.getSeat_ScheduleByScheduleId(scheduleId);
            List<Seat_ScheduleResponse> seatScheduleResponses=new ArrayList<>();
            for(Seat_Schedule ss:seatSchedules){
                seatScheduleResponses.add(seatScheduleService.getSeat_ScheduleResponse(ss));
            }
            return ResponseEntity.ok(seatScheduleResponses);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public static Set<Integer> current = new HashSet<>();
    public static Set<Integer> waitPayment = new HashSet<>();
    public static String prevString = "";
    public static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @MessageMapping( "/reserve")
    @SendTo("/topic/seats")
    public String sendMessage(@Payload String seat) throws IOException {
        System.out.println("---------list seat----------" + seat);
        String lastFiveChars = seat.length() < 5 ? seat : seat.substring(seat.length() - 5);
        String lastSevenChars = seat.length() < 7 ? seat : seat.substring(seat.length() - 7);
        Runnable task = () -> {
            System.out.println("Đã hết 1 phút! Thực hiện xử lý ở hàm B.");
            // Thực hiện các hành động bạn muốn trong hàm B
            for (Integer x : waitPayment) {
                System.out.print(x + " ");
                current.remove(x);
            }
            System.out.println();
            for(Integer x : current) {
                System.out.print(x + " ");
            }
            prevString = "donePayment";
        };
        if (lastSevenChars.equals("payment")) {
            seat = seat.substring(0, seat.length()-7);
            seat = seat.substring(1, seat.length()-1);
            String[] stringArray = seat.split(",");

            // Khởi tạo mảng số nguyên với độ dài tương ứng
            int[] intArray = new int[stringArray.length];
            for (int i = 0; i < stringArray.length; i++) {
                intArray[i] = Integer.parseInt(stringArray[i]);
                waitPayment.add(intArray[i]);
            }
            prevString = "waitPayment";
            scheduler.schedule(task, 15, TimeUnit.MINUTES);
            return setToString();
        }
        if (lastFiveChars.equals("leave")) {
            if (prevString.equals("waitPayment")) {
                prevString = "";
                return setToString();
            }
            if (seat.equals("[]leave")) {
                return setToString();
            }
            seat = seat.substring(0, seat.length()-5);
            seat = seat.substring(1, seat.length()-1);
            removeSeat(seat);
            return setToString();
        }
        if (seat.equals("join")) {
            if (lastFiveChars.equals("leave")) {
                current = new HashSet<>();
            }
            return setToString();
        } else if (seat.equals("[]leave")) {
            current = new HashSet<>();
            return setToString();
        } else {
            int seatNew = Integer.parseInt(seat.substring(1, seat.length()-1));
            if (current.contains(seatNew)) current.remove(seatNew);
            else current.add(seatNew);
        }
        return setToString();
    }

    public String setToString() {
        if (current.isEmpty()) return "[]";
        String res = "[";
        for(int x : current) {
            res += x + ",";
        }
        res.substring(0,res.length()-1);
        res+="]";
        return res;
    }

    public void removeSeat(String seat) {
        String[] stringArray = seat.split(",");

        // Khởi tạo mảng số nguyên với độ dài tương ứng
        int[] intArray = new int[stringArray.length];

        // Chuyển đổi từng phần tử từ chuỗi sang số nguyên
        for (int i = 0; i < stringArray.length; i++) {
            intArray[i] = Integer.parseInt(stringArray[i]);
            current.remove(intArray[i]);
        }
    }
}
