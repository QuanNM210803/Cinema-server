package com.example.cinemaserver.controller;

import com.example.cinemaserver.request.AreaRequest;
import com.example.cinemaserver.model.Area;
import com.example.cinemaserver.response.AreaResponse;
import com.example.cinemaserver.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/areas")
@RequiredArgsConstructor
public class AreaController {
    private final AreaService areaService;
    @GetMapping("/all")
    public ResponseEntity<?> getAreas(){
        List<Area> areas=areaService.getAreas();
        List<AreaResponse> areaResponses=new ArrayList<>();
        for(Area area:areas){
            areaResponses.add(areaService.getAreaResponse(area));
        }
        return ResponseEntity.ok(areaResponses);
    }

    @GetMapping("/{areaId}")
    public ResponseEntity<?> getArea(@PathVariable("areaId") Long id){
        try{
            Area area=areaService.getArea(id);
            AreaResponse areaResponses=areaService.getAreaResponse(area);
            return ResponseEntity.ok(areaResponses);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PostMapping("/addNew")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addNewArea(@ModelAttribute AreaRequest areaRequest){
        try {
            Area area=areaService.addNewArea(areaRequest);
            AreaResponse areaResponse=areaService.getAreaResponse(area);
            return ResponseEntity.ok(areaResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

    }
    @PutMapping("/update/{areaId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateArea(@PathVariable("areaId") Long id
                                        ,@ModelAttribute AreaRequest areaRequest){
        try {
            Area area=areaService.updateArea(id,areaRequest);
            AreaResponse areaResponse=areaService.getAreaResponse(area);
            return ResponseEntity.ok(areaResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
