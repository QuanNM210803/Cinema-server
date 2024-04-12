package com.example.cinemaserver.controller;

import com.example.cinemaserver.Exception.UserAlreadyExistsException;
import com.example.cinemaserver.Request.LoginRequest;
import com.example.cinemaserver.Request.UserRequest;
import com.example.cinemaserver.response.JwtResponse;
import com.example.cinemaserver.security.jwt.JwtUtils;
import com.example.cinemaserver.security.user.CinemaUserDetails;
import com.example.cinemaserver.service.ForgotPasswordService;
import com.example.cinemaserver.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final ForgotPasswordService forgotPasswordService;
    @PostMapping("/registerUser")
    public ResponseEntity<String> addNewUser(@ModelAttribute UserRequest userRequest){
        try{
            userService.registerUser(userRequest);
            return ResponseEntity.ok("Register successfully.");
        }catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        //xac thuc
        Authentication authentication= authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));

        //cho phép Spring Security biết được người dùng hiện tại đã xác thực là ai và có quyền truy cập gì trong suốt quá trình xử lý yêu cầu
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt= jwtUtils.generateJwtTokenForUser(authentication);
        CinemaUserDetails userDetails= (CinemaUserDetails) authentication.getPrincipal();
        List<String> roles=userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();
        return ResponseEntity.ok(new JwtResponse(
                userDetails.getId(),
                userDetails.getEmail(),
                jwt,
                roles
        ));
    }

    @PutMapping("/resetPassword/{email}")
    public ResponseEntity<?> resetPassword(@PathVariable("email") String email){
        try {
            forgotPasswordService.sendForgotPasswordEmail(email);
            return ResponseEntity.ok("Check email to regain password");
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }
}
