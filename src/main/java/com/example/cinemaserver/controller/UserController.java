package com.example.cinemaserver.controller;

import com.example.cinemaserver.Exception.UserAlreadyExistsException;
import com.example.cinemaserver.Request.UserRequest;
import com.example.cinemaserver.model.Branch;
import com.example.cinemaserver.model.User;
import com.example.cinemaserver.response.BranchResponse;
import com.example.cinemaserver.response.UserResponse;
import com.example.cinemaserver.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
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
            userResponses.add(userService.getUserResponse(user));
        }
        return ResponseEntity.ok(userResponses);
    }
    @GetMapping("/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable("email") String email){
        try{
            User user=userService.getUser(email);
            UserResponse userResponse=userService.getUserResponse(user);
            return ResponseEntity.ok(userResponse);
//        }catch (UsernameNotFoundException e){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fectching user");
        }
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable("email") String email){
        try{
            userService.deleteUser(email);
            return ResponseEntity.ok("User deleted successfully.");
//        }catch (UsernameNotFoundException e){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fectching user");
        }
    }
    @PostMapping("/addNew")
    public ResponseEntity<String> addNewUser(@ModelAttribute UserRequest userRequest){
        try{
            userService.registerUser(userRequest);
            return ResponseEntity.ok("Register successfully.");
        }catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("userId") Long id
            , @ModelAttribute UserRequest userRequest
    ) throws SQLException, IOException {
        User user=userService.updateUser(id,userRequest);
        UserResponse userResponse=userService.getUserResponse(user);
        return ResponseEntity.ok(userResponse);
    }
}
