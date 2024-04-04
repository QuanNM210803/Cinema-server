package com.example.cinemaserver.controller;

import com.example.cinemaserver.Request.BranchRequest;
import com.example.cinemaserver.model.Branch;
import com.example.cinemaserver.response.BranchResponse;
import com.example.cinemaserver.service.IBranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/branches")
@RequiredArgsConstructor
public class BranchController {
    private final IBranchService branchService;
    @GetMapping("/all")
    public ResponseEntity<List<BranchResponse>> getAllBranchs() throws SQLException {
        List<Branch> branches=branchService.getAll();
        List<BranchResponse> branchResponses=new ArrayList<>();
        for(Branch branch:branches){
            branchResponses.add(branchService.getBranchResponse(branch));
        }
        return ResponseEntity.ok(branchResponses);
    }
    @GetMapping("/all/{areaId}")
    public ResponseEntity<?> getBranchByAreaId(@PathVariable("areaId") Long areaId){
        try{
            List<Branch> branches=branchService.getBranchByAreaId(areaId);
            List<BranchResponse> branchResponses=new ArrayList<>();
            for(Branch branch:branches){
                branchResponses.add(branchService.getBranchResponse(branch));
            }
            return ResponseEntity.ok(branchResponses);
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }
    // lay rap theo phim va khu vuc, dung cho qua trinh dat ve
    @GetMapping("/all/client/movieAndArea/{movieId}/{areaId}")
    public ResponseEntity<?> getBranchClientByMovieIdAndAreaId(@PathVariable("movieId") Long movieId
                                                        ,@PathVariable("areaId") Long areaId){
        try{
            List<Branch> branches=branchService.getBranchClientByMovieIdAndAreaId(movieId,areaId);
            List<BranchResponse> branchResponses=new ArrayList<>();
            for(Branch branch:branches){
                branchResponses.add(branchService.getBranchResponse(branch));
            }
            return ResponseEntity.ok(branchResponses);
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/{branchId}")
    public ResponseEntity<?> getBranch(@PathVariable("branchId") Long id){
        try {
            Branch branch=branchService.getBranch(id);
            BranchResponse branchResponse=branchService.getBranchResponse(branch);
            return ResponseEntity.ok(branchResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching branch.");
        }
    }
    @PostMapping("/addNew/{areaId}")
    public ResponseEntity<String> addNewBranch(@PathVariable("areaId") Long areaId
                                            ,@ModelAttribute BranchRequest branchRequest) {
        try{
            branchService.addNewBranch(areaId,branchRequest);
            return ResponseEntity.ok("Add branch successfully.");
        }catch (Exception e){
            throw new RuntimeException("Error");
        }
    }
    //viet de day thoi, chu dung xoa branch, thay vao do hay update status ve false
    @DeleteMapping("/delete/{branchId}")
    public ResponseEntity<String> deleteBranch(@PathVariable("branchId") Long id){
        return branchService.deleteBranch(id);
    }

    @PutMapping("/update/{branchId}")
    public ResponseEntity<BranchResponse> updateBranch(@PathVariable("branchId") Long id
                                                       ,@ModelAttribute BranchRequest branchRequest
    ) throws SQLException, IOException {
        Branch branch=branchService.updateBranch(id,branchRequest);
        BranchResponse branchResponse=branchService.getBranchResponse(branch);
        return ResponseEntity.ok(branchResponse);
    }
}
