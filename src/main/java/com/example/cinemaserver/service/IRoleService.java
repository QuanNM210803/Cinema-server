package com.example.cinemaserver.service;

import com.example.cinemaserver.model.Role;
import com.example.cinemaserver.model.User;

import java.util.List;

public interface IRoleService {
    List<Role> getRoles();

    void addNewRole(Role role);

    void deleteRole(Long roleId);

    Role removeAllUserFromRole(Long roleId);

    User removeUserFromRole(Long userId, Long roleId);

    User assignRoleToUser(Long userId, Long roleId);
}
