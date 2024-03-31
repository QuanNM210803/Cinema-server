package com.example.cinemaserver.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private String name;
    private Boolean status;
    private String photo;
    private BranchResponse branchResponse;
}
