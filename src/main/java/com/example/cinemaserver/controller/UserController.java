package com.example.cinemaserver.controller;

import com.example.cinemaserver.request.AdminUpdateUserRequest;
import com.example.cinemaserver.request.UserUpdateUserRequest;
import com.example.cinemaserver.model.User;
import com.example.cinemaserver.response.UserResponse;
import com.example.cinemaserver.service.IUserService;
import com.example.cinemaserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserResponse>> getUsers() throws SQLException {
        List<User> users=userService.getUsers();
        List<UserResponse> userResponses=new ArrayList<>();
        for(User user:users){
            userResponses.add(userService.getUserResponseNonePhoto(user));
        }
        return ResponseEntity.ok(userResponses);
    }
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
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
    @PreAuthorize("@userService.getUserById(#userId).email==principal.username")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") Long userId){
        try{
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PutMapping("/update/admin/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
    @PreAuthorize("@userService.getUserById(#id).email==principal.username")
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
