package com.example.cinemaserver.controller;

import com.example.cinemaserver.Request.AreaRequest;
import com.example.cinemaserver.model.Area;
import com.example.cinemaserver.response.AreaResponse;
import com.example.cinemaserver.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
        try{
            List<Area> areas=areaService.getAreas();
            List<AreaResponse> areaResponses=new ArrayList<>();
            for(Area area:areas){
                areaResponses.add(areaService.getAreaResponse(area));
            }
            return ResponseEntity.ok(areaResponses);
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/{areaId}")
    public ResponseEntity<?> getArea(@PathVariable("areaId") Long id){
        try{
            Area area=areaService.getArea(id);
            AreaResponse areaResponses=areaService.getAreaResponse(area);
            return ResponseEntity.ok(areaResponses);
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }
    @PostMapping("/addNew")
    public ResponseEntity<?> addNewArea(@ModelAttribute AreaRequest areaRequest){
        try{
            areaService.addNewArea(areaRequest);
            return ResponseEntity.ok("Add area successfully.");
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }
    @PutMapping("/update/{areaId}")
    public ResponseEntity<?> updateArea(@PathVariable("areaId") Long id
                                        ,@ModelAttribute AreaRequest areaRequest){
        try {
            Area area=areaService.updateArea(id,areaRequest);
            AreaResponse areaResponse=areaService.getAreaResponse(area);
            return ResponseEntity.ok(areaResponse);
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }
}
