package com.example.cinemaserver.service;

import com.example.cinemaserver.request.AreaRequest;
import com.example.cinemaserver.model.Area;
import com.example.cinemaserver.repository.AreaRepository;
import com.example.cinemaserver.response.AreaResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.lang.module.FindException;
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
        return areaRepository.findById(id).orElseThrow(()->new FindException("Not found Area."));
    }

    @Override
    public Area addNewArea(AreaRequest areaRequest) {
        if(areaRepository.existsByName(areaRequest.getName())){
            throw new RuntimeException(areaRequest.getName()+" already exists.");
        }
        Area area=new Area(areaRequest.getName());
        return areaRepository.save(area);
    }

    @Override
    public Area updateArea(Long id, AreaRequest areaRequest) {
        Area area=this.getArea(id);
        if(!StringUtils.isBlank(areaRequest.getName())){
            if(areaRepository.existsByName(areaRequest.getName())){
                throw new RuntimeException(areaRequest.getName()+" already exists.");
            }
            area.setName(areaRequest.getName());
        }
        return areaRepository.save(area);
    }

    @Override
    public AreaResponse getAreaResponse(Area area) {
        return new AreaResponse(area.getId(), area.getName());
    }


}
