package com.example.cinemaserver.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchResponse {
    private Long id;
    private String name;
    private String address;
    private String introduction;
    private Double revenue;
    private Long numberOfTickets;
    private String photo;
    private Boolean status;
    private int numberOfRooms;
    private AreaResponse areaResponse;
}
