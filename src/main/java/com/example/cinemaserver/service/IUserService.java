package com.example.cinemaserver.service;

import com.example.cinemaserver.request.RegisterUserRequest;
import com.example.cinemaserver.request.AdminUpdateUserRequest;
import com.example.cinemaserver.request.UserUpdateUserRequest;
import com.example.cinemaserver.model.User;
import com.example.cinemaserver.response.UserResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IUserService {
    List<User> getUsers();
    void deleteUser(Long userId);
    User registerUser(RegisterUserRequest userRequest) throws IOException, SQLException;

    String getAvatar(User user) throws SQLException;

    UserResponse getUserResponse(User user) throws SQLException;

    User adminUpdateUser(Long id, AdminUpdateUserRequest updateUserRequest) throws IOException, SQLException;

    User getUserById(Long userId);

    UserResponse getUserResponseNonePhoto(User user) throws SQLException;

    User userUpdateUser(Long id, UserUpdateUserRequest updateUserRequest) throws IOException, SQLException;
}
