package com.example.cinemaserver.service;

import com.example.cinemaserver.exception.RoleAlreadyExistsException;
import com.example.cinemaserver.exception.UserAlreadyExistsException;
import com.example.cinemaserver.model.Role;
import com.example.cinemaserver.model.User;
import com.example.cinemaserver.repository.RoleRepository;
import com.example.cinemaserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.lang.module.FindException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role addNewRole(Role therole) {
        String roleName="ROLE_"+therole.getName().toUpperCase();
        if(roleRepository.existsByName(roleName)){
            throw new RoleAlreadyExistsException(roleName+" role already exists.");
        }
        Role role=new Role(roleName);
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(Long roleId) {
        this.removeAllUserFromRole(roleId);
        roleRepository.deleteById(roleId);
    }

    public Role removeAllUserFromRole(Long roleId){
        Role role=roleRepository.findById(roleId).orElseThrow(()->new FindException("Not found role."));
        role.removeAllUserFromRole();
        return roleRepository.save(role);
    }

    @Override
    public User removeUserFromRole(Long userId, Long roleId) {
        Optional<User> user=userRepository.findById(userId);
        Optional<Role> role=roleRepository.findById(roleId);
        if(user.isEmpty()){
            throw new UsernameNotFoundException("Not found user.");
        }
        if(role.isEmpty()){
            throw new UsernameNotFoundException("Not found role.");
        }
        if(role.get().getUsers().contains(user.get())){
            role.get().removeUserFromRole(user.get());
            roleRepository.save(role.get());
            return user.get();
        }else {
            throw new RuntimeException(user.get().getFullName() +" doesn't have role "+role.get().getName());
        }
    }

    @Override
    public User assignRoleToUser(Long userId, Long roleId) {
        Optional<User> user=userRepository.findById(userId);
        Optional<Role> role=roleRepository.findById(roleId);
        if(user.isEmpty()){
            throw new UsernameNotFoundException("Not found user.");
        }
        if(role.isEmpty()){
            throw new UsernameNotFoundException("Not found role.");
        }
        if(user.get().getRoles().contains(role.get())){
            throw new UserAlreadyExistsException(user.get().getFullName()+" is already assign to the "+role.get().getName()+" role.");
        }
        role.get().assignRoleToUser(user.get());
        roleRepository.save(role.get());
        return user.get();
    }
}
