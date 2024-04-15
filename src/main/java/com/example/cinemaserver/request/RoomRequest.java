package com.example.cinemaserver.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public class RoomRequest {
    private String name;
    private Boolean status;
    private MultipartFile photo;
}
