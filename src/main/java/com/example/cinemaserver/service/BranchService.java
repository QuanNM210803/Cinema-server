package com.example.cinemaserver.service;

import com.example.cinemaserver.Exception.ResourceNotFoundException;
import com.example.cinemaserver.Request.BranchRequest;
import com.example.cinemaserver.model.Branch;
import com.example.cinemaserver.model.Room;
import com.example.cinemaserver.repository.BranchRepository;
import com.example.cinemaserver.repository.RoomRepository;
import com.example.cinemaserver.response.BranchResponse;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
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
    @Override
    public List<Branch> getAll() {
        return branchRepository.findAll();
    }
    @Override
    public Branch getBranch(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Branch not found"));
    }


    @Override
    public void addNewBranch(BranchRequest branchRequest) throws IOException, SQLException {
        Branch branch=new Branch();
        branch.setName(branchRequest.getName());
        branch.setAddress(branchRequest.getAddress());
        branch.setIntroduction(branchRequest.getIntroduction());
        if(!branchRequest.getPhoto().isEmpty()){
            byte[] bytes=branchRequest.getPhoto().getBytes();
            Blob blob=new SerialBlob(bytes);
            branch.setPhoto(blob);
        }
        branchRepository.save(branch);
    }

    @Override
    public ResponseEntity<String> deleteBranch(Long id) {
        try{
            Branch branch=branchRepository.findById(id).get();
            branchRepository.deleteById(id);
            return ResponseEntity.ok("Delete successfully.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching branch.");
        }
    }
    @Override
    public Branch updateBranch(Long id, BranchRequest branchRequest) throws IOException, SQLException {
        Branch branch=branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));;
        if(branchRequest.getName()!=null){
            branch.setName(branchRequest.getName());
        }
        if(branchRequest.getAddress()!=null){
            branch.setAddress(branchRequest.getAddress());
        }
        if(branchRequest.getIntroduction()!=null){
            branch.setIntroduction(branchRequest.getIntroduction());
        }
        if(!branchRequest.getPhoto().isEmpty()){
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
    }

    @Override
    public List<Branch> getBranchClientByMovieId(Long movieId) {
        return branchRepository.findBranchClientByMovieId(movieId, LocalDate.now(), LocalTime.now());
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
        return new BranchResponse(branch.getId(),branch.getName()
                                ,branch.getAddress(),branch.getIntroduction()
                                ,getBranchPhoto(branch),branch.getStatus());
    }

}
