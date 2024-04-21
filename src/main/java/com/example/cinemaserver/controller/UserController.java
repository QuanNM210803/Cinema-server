package com.example.cinemaserver.controller;

import com.example.cinemaserver.request.AdminUpdateUserRequest;
import com.example.cinemaserver.request.UserUpdateUserRequest;
import com.example.cinemaserver.model.User;
import com.example.cinemaserver.response.UserResponse;
import com.example.cinemaserver.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    public final IUserService userService;
    @GetMapping("/all")
    private ResponseEntity<List<UserResponse>> getUsers() throws SQLException {
        List<User> users=userService.getUsers();
        List<UserResponse> userResponses=new ArrayList<>();
        for(User user:users){
            userResponses.add(userService.getUserResponseNonePhoto(user));
        }
        return ResponseEntity.ok(userResponses);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable("userId") Long userId){
        try{
            User user=userService.getUserById(userId);
            UserResponse userResponse=userService.getUserResponse(user);
            return ResponseEntity.ok(userResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") Long userId){
        try{
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PutMapping("/update/admin/{userId}")
    public ResponseEntity<?> adminUpdateUser(@PathVariable("userId") Long id
                                            , @ModelAttribute AdminUpdateUserRequest updateUserRequest) {
        try{
            User user=userService.adminUpdateUser(id,updateUserRequest);
            UserResponse userResponse=userService.getUserResponse(user);
            return ResponseEntity.ok(userResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/update/user/{userId}")
    public ResponseEntity<?> userUpdateUser(@PathVariable("userId") Long id
                                            , @ModelAttribute UserUpdateUserRequest updateUserRequest) {
        try {
            User user=userService.userUpdateUser(id,updateUserRequest);
            UserResponse userResponse=userService.getUserResponse(user);
            return ResponseEntity.ok(userResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
