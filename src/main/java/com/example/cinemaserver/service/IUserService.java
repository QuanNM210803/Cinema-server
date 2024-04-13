package com.example.cinemaserver.service;

import com.example.cinemaserver.Request.RegisterUserRequest;
import com.example.cinemaserver.Request.UpdateUserRequest;
import com.example.cinemaserver.model.User;
import com.example.cinemaserver.response.UserResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IUserService {
    List<User> getUsers();

    User getUser(String email);

    void deleteUser(String email);


    void registerUser(RegisterUserRequest userRequest) throws IOException, SQLException;

    String getAvatar(User user) throws SQLException;

    UserResponse getUserResponse(User user) throws SQLException;

    User updateUser(Long id, UpdateUserRequest updateUserRequest) throws IOException, SQLException;

    User getUserById(Long userId);

    UserResponse getUserResponseNonePhoto(User user) throws SQLException;
}
