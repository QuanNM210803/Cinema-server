package com.example.cinemaserver.controller;

import com.example.cinemaserver.request.BranchRequest;
import com.example.cinemaserver.model.Branch;
import com.example.cinemaserver.response.BranchResponse;
import com.example.cinemaserver.service.IBranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.module.FindException;
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
                branchResponses.add(branchService.getBranchResponseNonePhoto(branch));
            }
            return ResponseEntity.ok(branchResponses);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    // lay rap theo phim va khu vuc, dung cho qua trinh dat ve
    @GetMapping("/all/client/movieAndArea/{movieId}/{areaId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> getBranchClientByMovieIdAndAreaId(@PathVariable("movieId") Long movieId
                                                        ,@PathVariable("areaId") Long areaId){
        try{
            List<Branch> branches=branchService.getBranchClientByMovieIdAndAreaId(movieId,areaId);
            List<BranchResponse> branchResponses=new ArrayList<>();
            for(Branch branch:branches){
                branchResponses.add(branchService.getBranchResponseNonePhoto(branch));
            }
            return ResponseEntity.ok(branchResponses);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{branchId}")
    public ResponseEntity<?> getBranch(@PathVariable("branchId") Long id){
        try {
            Branch branch=branchService.getBranch(id);
            BranchResponse branchResponse=branchService.getBranchResponse(branch);
            return ResponseEntity.ok(branchResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PostMapping("/addNew/{areaId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addNewBranch(@PathVariable("areaId") Long areaId
                                            ,@ModelAttribute BranchRequest branchRequest) {
        try{
            Branch branch=branchService.addNewBranch(areaId,branchRequest);
            BranchResponse branchResponse=branchService.getBranchResponse(branch);
            return ResponseEntity.ok(branchResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    //viet de day thoi, chu dung xoa branch, thay vao do hay update status ve false
    @DeleteMapping("/delete/{branchId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteBranch(@PathVariable("branchId") Long id){
        try{
            branchService.deleteBranch(id);
            return ResponseEntity.ok("Delete successfully.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/update/{branchId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateBranch(@PathVariable("branchId") Long id
                                                       ,@ModelAttribute BranchRequest branchRequest
    ) throws SQLException, IOException {
        try{
            Branch branch=branchService.updateBranch(id,branchRequest);
            BranchResponse branchResponse=branchService.getBranchResponse(branch);
            return ResponseEntity.ok(branchResponse);
        }catch (FindException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
