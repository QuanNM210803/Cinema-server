package com.example.cinemaserver.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponse {
    private Long id;
    private String name;
    private Double price;
    private RoomResponse roomResponse;
}
