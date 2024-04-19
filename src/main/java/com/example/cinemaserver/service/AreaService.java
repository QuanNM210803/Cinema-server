package com.example.cinemaserver.service;

import com.example.cinemaserver.exception.ResourceNotFoundException;
import com.example.cinemaserver.request.AreaRequest;
import com.example.cinemaserver.model.Area;
import com.example.cinemaserver.repository.AreaRepository;
import com.example.cinemaserver.response.AreaResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AreaService implements IAreaService{
    private final AreaRepository areaRepository;
    @Override
    public List<Area> getAreas() {
        return areaRepository.findAll();
    }
    @Override
    public Area getArea(Long id) {
        return areaRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Not found Area."));
    }

    @Override
    public Area addNewArea(AreaRequest areaRequest) {
        Area area=new Area(areaRequest.getName());
        return areaRepository.save(area);
    }

    @Override
    public Area updateArea(Long id, AreaRequest areaRequest) {
        Area area=areaRepository.findById(id).get();
        if(!StringUtils.isBlank(areaRequest.getName())){
            area.setName(areaRequest.getName());
        }
        return areaRepository.save(area);
    }

    @Override
    public AreaResponse getAreaResponse(Area area) {
        return new AreaResponse(area.getId(), area.getName());
    }


}
