package com.example.cinemaserver.service;

import com.example.cinemaserver.request.AreaRequest;
import com.example.cinemaserver.model.Area;
import com.example.cinemaserver.response.AreaResponse;

import java.util.List;

public interface IAreaService {
    List<Area> getAreas();

    AreaResponse getAreaResponse(Area area);

    Area getArea(Long id);

    void addNewArea(AreaRequest areaRequest);

    Area updateArea(Long id, AreaRequest areaRequest);
}
