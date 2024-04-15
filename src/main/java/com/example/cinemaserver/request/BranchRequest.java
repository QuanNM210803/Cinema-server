package com.example.cinemaserver.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public class BranchRequest {
    private String name;
    private String address;
    private String introduction;
    private MultipartFile photo;
    private Boolean status;
}
