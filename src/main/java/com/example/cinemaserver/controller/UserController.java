package com.example.cinemaserver.controller;

import com.example.cinemaserver.Request.AdminUpdateUserRequest;
import com.example.cinemaserver.Request.UserUpdateUserRequest;
import com.example.cinemaserver.model.User;
import com.example.cinemaserver.response.UserResponse;
import com.example.cinemaserver.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    @GetMapping("/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable("email") String email){
        try{
            User user=userService.getUser(email);
            UserResponse userResponse=userService.getUserResponse(user);
            return ResponseEntity.ok(userResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fectching user");
        }
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable("email") String email){
        try{
            userService.deleteUser(email);
            return ResponseEntity.ok("User deleted successfully.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fectching user");
        }
    }


    @PutMapping("/update/admin/{userId}")
    public ResponseEntity<UserResponse> adminUpdateUser(@PathVariable("userId") Long id
            , @ModelAttribute AdminUpdateUserRequest updateUserRequest) throws SQLException, IOException {
        User user=userService.adminUpdateUser(id,updateUserRequest);
        UserResponse userResponse=userService.getUserResponse(user);
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/update/user/{userId}")
    public ResponseEntity<UserResponse> userUpdateUser(@PathVariable("userId") Long id
            , @ModelAttribute UserUpdateUserRequest updateUserRequest) throws SQLException, IOException {
        User user=userService.userUpdateUserRequest(id,updateUserRequest);
        UserResponse userResponse=userService.getUserResponse(user);
        return ResponseEntity.ok(userResponse);
    }
}
