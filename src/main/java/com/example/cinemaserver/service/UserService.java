package com.example.cinemaserver.service;

import com.example.cinemaserver.Exception.ResourceNotFoundException;
import com.example.cinemaserver.Exception.UserAlreadyExistsException;
import com.example.cinemaserver.Request.UserRequest;
import com.example.cinemaserver.model.Role;
import com.example.cinemaserver.model.User;
import com.example.cinemaserver.repository.RoleRepository;
import com.example.cinemaserver.repository.UserRepository;
import com.example.cinemaserver.response.UserResponse;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
    private final PasswordEncoder passwordEncoder;
    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(()->new UserAlreadyExistsException("User not found"));
    }
    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(()->new UserAlreadyExistsException("User not found"));
    }

    @Override
    public void deleteUser(String email) {
        this.removeAllRoleFromUser(email);
        Optional<User> user= userRepository.findByEmail(email);
        user.ifPresent(theUser -> userRepository.deleteById(theUser.getId()));
    }

    @Override
    public void registerUser(UserRequest userRequest) throws IOException, SQLException {
        if(userRepository.existsByEmail(userRequest.getEmail())){
            throw new UserAlreadyExistsException(userRequest.getEmail()+" already exists");
        }
        User user=new User();
        user.setFullName(userRequest.getFullName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setDob(userRequest.getDob());
        if(!userRequest.getPhoto().isEmpty()){
            byte[] bytes= userRequest.getPhoto().getBytes();
            Blob blob=new SerialBlob(bytes);
            user.setAvatar(blob);
        }else {
            user.formatImageToBlob();
        }
        Role userRole=roleRepository.findByName("ROLE_USER").get();
        user.setRoles(Collections.singletonList(userRole));
        userRepository.save(user);
    }
    @Override
    public User updateUser(Long id, UserRequest userRequest) throws IOException, SQLException {
        User user= userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User not found."));
        if(userRequest.getFullName()!=null){
            user.setFullName(userRequest.getFullName());
        }
        if(userRequest.getEmail()!=null){
            user.setEmail(userRequest.getEmail());
        }
        if(userRequest.getPassword()!=null){
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        if(userRequest.getDob()!=null){
            user.setDob(userRequest.getDob());
        }
        if(!userRequest.getPhoto().isEmpty()){
            byte[] bytes=userRequest.getPhoto().getBytes();
            Blob blob=new SerialBlob(bytes);
            user.setAvatar(blob);
        }
        return userRepository.save(user);
    }

    public void removeAllRoleFromUser(String email){
        Optional<User> user= userRepository.findByEmail(email);
        user.ifPresent(User::removeAllRoleFromUser);
        userRepository.save(user.get());
    }
    @Override
    public String getAvatar(User user) throws SQLException {
        Blob blob=user.getAvatar();
        byte[] photoBytes = blob!=null ? blob.getBytes(1,(int)blob.length()):null;
        String base64photo = photoBytes!=null && photoBytes.length>0 ?  Base64.encodeBase64String(photoBytes):"";
        return base64photo;
    }

    @Override
    public UserResponse getUserResponse(User user) throws SQLException {
        DateTimeFormatter formatDate= DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return new UserResponse(user.getId(),user.getFullName()
                ,user.getEmail(),user.getPassword()
                ,user.getDob().format(formatDate),getAvatar(user)
                ,user.getAge(),user.getRoles());
    }


}
