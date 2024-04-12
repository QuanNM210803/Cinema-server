package com.example.cinemaserver.service;

import com.example.cinemaserver.Request.UserRequest;
import com.example.cinemaserver.model.User;
import com.example.cinemaserver.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface IUserService {
    List<User> getUsers();

    User getUser(String email);

    void deleteUser(String email);


    void registerUser(UserRequest userRequest) throws IOException, SQLException;

    String getAvatar(User user) throws SQLException;

    UserResponse getUserResponse(User user) throws SQLException;

    User updateUser(Long id, UserRequest userRequest) throws IOException, SQLException;

    User getUserById(Long userId);

    UserResponse getUserResponseNonePhoto(User user) throws SQLException;
}
