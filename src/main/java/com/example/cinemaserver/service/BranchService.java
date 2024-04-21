package com.example.cinemaserver.service;

import com.example.cinemaserver.model.*;
import com.example.cinemaserver.repository.ScheduleRepository;
import com.example.cinemaserver.request.BranchRequest;
import com.example.cinemaserver.repository.BranchRepository;
import com.example.cinemaserver.repository.RoomRepository;
import com.example.cinemaserver.repository.TicketRepository;
import com.example.cinemaserver.response.AreaResponse;
import com.example.cinemaserver.response.BranchResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.lang.module.FindException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService implements IBranchService{
    private final BranchRepository branchRepository;
    private final RoomRepository roomRepository;
    private final AreaService areaService;
    private final TicketRepository ticketRepository;
    private final ScheduleRepository scheduleRepository;
    private final MovieService movieService;
    @Override
    public List<Branch> getAll() {
        return branchRepository.findAll();
    }
    @Override
    public Branch getBranch(Long id) {
        return branchRepository.findById(id).orElseThrow(()->new FindException("Not found branch."));
    }
    @Override
    public Branch addNewBranch(Long areaId, BranchRequest branchRequest) throws IOException, SQLException {
        Branch branch=new Branch();
        branch.setName(branchRequest.getName());
        branch.setAddress(branchRequest.getAddress());
        branch.setIntroduction(branchRequest.getIntroduction());
        if(!branchRequest.getPhoto().isEmpty() && branchRequest.getPhoto()!=null){
            byte[] bytes=branchRequest.getPhoto().getBytes();
            Blob blob=new SerialBlob(bytes);
            branch.setPhoto(blob);
        }
        Area area=areaService.getArea(areaId);
        branch.setArea(area);
        return branchRepository.save(branch);
    }

    @Override
    public void deleteBranch(Long id) {
        this.getBranch(id);
        branchRepository.deleteById(id);
    }
    @Override
    public Branch updateBranch(Long id, BranchRequest branchRequest) throws IOException, SQLException {
        Branch branch=branchRepository.findById(id)
                .orElseThrow(() -> new FindException("Not found branch."));
        List<Schedule> futureSchedules=scheduleRepository.findSchedulesFutureByBranch(id,LocalDate.now(),LocalTime.now());
        if(branchRequest.getStatus() || futureSchedules.size()==0){
            if(!StringUtils.isBlank(branchRequest.getName())){
                branch.setName(branchRequest.getName());
            }
            if(!StringUtils.isBlank(branchRequest.getAddress())){
                branch.setAddress(branchRequest.getAddress());
            }
            if(!StringUtils.isBlank(branchRequest.getIntroduction())){
                branch.setIntroduction(branchRequest.getIntroduction());
            }
            if(!branchRequest.getPhoto().isEmpty() && branchRequest.getPhoto()!=null){
                byte[] bytes=branchRequest.getPhoto().getBytes();
                Blob blob=new SerialBlob(bytes);
                branch.setPhoto(blob);
            }
            if(!branchRequest.getStatus() && branch.getStatus()){
                setStatusRooms(false,branch.getId());
            }
            else if(branchRequest.getStatus() && !branch.getStatus()){
                setStatusRooms(true,branch.getId());
            }
            branch.setStatus(branchRequest.getStatus());
            return branchRepository.save(branch);
        }else {
            throw new RuntimeException("Cannot set inactive status because showtime that haven't been broadcast yet exist.");
        }
    }

    @Override
    public List<Branch> getBranchClientByMovieIdAndAreaId(Long movieId,Long areaId) {
        areaService.getArea(areaId);
        movieService.getMovie(movieId);
        return branchRepository.findBranchClientByMovieIdAndAreaId(movieId,areaId, LocalDate.now(), LocalTime.now());
    }

    @Override
    public List<Branch> getBranchByAreaId(Long areaId) {
        areaService.getArea(areaId);
        return branchRepository.findBranchByAreaId(areaId);
    }

    public void setStatusRooms(Boolean b,Long branchid){
        List<Room> rooms=roomRepository.findAllRoomsByBranchId(branchid);
        for (Room room:rooms){
            room.setStatus(b);
            roomRepository.save(room);
        }
    }
    @Override
    public String getBranchPhoto(Branch branch) throws SQLException {
        Blob blob=branch.getPhoto();
        byte[] photoBytes = blob!=null ? blob.getBytes(1,(int)blob.length()):null;
        String base64photo = photoBytes!=null && photoBytes.length>0 ?  Base64.encodeBase64String(photoBytes):"";
        return base64photo;
    }

    @Override
    public BranchResponse getBranchResponse(Branch branch) throws SQLException {
        Area area=branch.getArea();
        AreaResponse areaResponse=areaService.getAreaResponse(area);
        List<Ticket> tickets=ticketRepository.findTicketsByBranchId(branch.getId());
        return new BranchResponse(branch.getId()
                                ,branch.getName()
                                ,branch.getAddress()
                                ,branch.getIntroduction()
                                ,tickets.stream().mapToDouble(Ticket::getPrice).sum()
                                , (long) tickets.size()
                                ,getBranchPhoto(branch)
                                ,branch.getStatus()
                                ,areaResponse);
    }

    @Override
    public BranchResponse getBranchResponseNonePhoto(Branch branch) {
        Area area=branch.getArea();
        AreaResponse areaResponse=areaService.getAreaResponse(area);
        List<Ticket> tickets=ticketRepository.findTicketsByBranchId(branch.getId());
        return new BranchResponse(branch.getId()
                ,branch.getName()
                ,branch.getAddress()
                ,branch.getIntroduction()
                ,tickets.stream().mapToDouble(Ticket::getPrice).sum()
                , (long) tickets.size()
                ,null
                ,branch.getStatus()
                ,areaResponse);
    }
}
