package com.example.cinemaserver.service;

import com.example.cinemaserver.Exception.RoleAlreadyExistsException;
import com.example.cinemaserver.Exception.UserAlreadyExistsException;
import com.example.cinemaserver.model.Role;
import com.example.cinemaserver.model.User;
import com.example.cinemaserver.repository.RoleRepository;
import com.example.cinemaserver.repository.UserRepositoty;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleSerivce{
    private final RoleRepository roleRepository;
    private final UserRepositoty userRepository;
    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public void addNewRole(Role therole) {
        String roleName="ROLE_"+therole.getName().toUpperCase();
        Role role=new Role(roleName);
        if(roleRepository.existsByName(roleName)){
            throw new RoleAlreadyExistsException(therole.getName()+" role already exists");
        }
        roleRepository.save(role);
    }

    @Override
    public void deleteRole(Long roleId) {
        this.removeAllUserFromRole(roleId);
        roleRepository.deleteById(roleId);
    }

    public Role removeAllUserFromRole(Long roleId){
        Optional<Role> role=roleRepository.findById(roleId);
        role.ifPresent(Role::removeAllUserFromRole);
        return roleRepository.save(role.get());
    }

    @Override
    public User removeUserFromRole(Long userId, Long roleId) {
        Optional<User> user=userRepository.findById(userId);
        Optional<Role> role=roleRepository.findById(roleId);
        if(role.isPresent() && role.get().getUsers().contains(user.get())){
            role.get().removeUserFromRole(user.get());
            roleRepository.save(role.get());
            return user.get();
        }
//        throw new UsernameNotFoundException("User not found");
        throw new UserAlreadyExistsException("User not found");
    }

    @Override
    public User assignRoleToUser(Long userId, Long roleId) {
        Optional<User> user=userRepository.findById(userId);
        Optional<Role> role=roleRepository.findById(roleId);
        if(user.isPresent() && user.get().getRoles().contains(role.get())){
            throw new UserAlreadyExistsException(
                    user.get().getFullName()+" is already assign to the "+role.get().getName()+" role");
        }
        if(role.isPresent()){
            role.get().assignRoleToUser(user.get());
            roleRepository.save(role.get());
        }
        return user.get();
    }
}
