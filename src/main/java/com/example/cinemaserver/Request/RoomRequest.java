package com.example.cinemaserver.Request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public class RoomRequest {
    private String name;
    private Boolean status;
    private MultipartFile photo;
}
