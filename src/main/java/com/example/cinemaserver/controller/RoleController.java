package com.example.cinemaserver.controller;

import com.example.cinemaserver.Exception.RoleAlreadyExistsException;
import com.example.cinemaserver.model.Role;
import com.example.cinemaserver.model.User;
import com.example.cinemaserver.response.UserResponse;
import com.example.cinemaserver.service.IRoleSerivce;
import com.example.cinemaserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final IRoleSerivce roleService;
    private final UserService userService;
    @GetMapping("/all")
    public ResponseEntity<List<Role>> getAllRoles(){
        return ResponseEntity.ok(roleService.getRoles());
    }
    @PostMapping("/addNew")
    public ResponseEntity<String> addNewRole(@RequestBody Role role){
        try{
            roleService.addNewRole(role);
            return ResponseEntity.ok("New role created successfully.");

        }catch (RoleAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{roleId}")
    public void deleteRole(@PathVariable("roleId") Long roleId){
        roleService.deleteRole(roleId);
    }

    @PostMapping("/remove-all-user-from-role/{roleId}")
    public ResponseEntity<?> removeAllUserFromRole(@PathVariable("roleId") Long roleId){
        try {
            return ResponseEntity.ok(roleService.removeAllUserFromRole(roleId));
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }

    }

    @PostMapping("/remove-user-from-role/{userId}/{roleId}")
    public ResponseEntity<?> removeUserFromRole(@PathVariable("userId") Long userId,
                                   @PathVariable("roleId") Long roleId){
        try{
            User user=roleService.removeUserFromRole(userId,roleId);
            UserResponse userResponse=userService.getUserResponse(user);
            return ResponseEntity.ok(userResponse);
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }
    @PostMapping("/assign-role-to-user/{roleId}/{userId}")
    public ResponseEntity<?> assignRoleToUser(@PathVariable("userId") Long userId,
                                                         @PathVariable("roleId") Long roleId){
        try{
            User user=roleService.assignRoleToUser(userId,roleId);
            UserResponse userResponse=userService.getUserResponse(user);
            return ResponseEntity.ok(userResponse);
        }catch (Exception e){
            return ResponseEntity.ok(e.getMessage());
        }
    }
}
