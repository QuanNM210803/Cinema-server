package com.example.cinemaserver.service;

import com.example.cinemaserver.exception.UserAlreadyExistsException;
import com.example.cinemaserver.model.Ticket;
import com.example.cinemaserver.repository.TicketRepository;
import com.example.cinemaserver.request.RegisterUserRequest;
import com.example.cinemaserver.request.AdminUpdateUserRequest;
import com.example.cinemaserver.request.UserUpdateUserRequest;
import com.example.cinemaserver.model.Role;
import com.example.cinemaserver.model.User;
import com.example.cinemaserver.repository.RoleRepository;
import com.example.cinemaserver.repository.UserRepository;
import com.example.cinemaserver.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.lang.module.FindException;
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
    private final IRoleService roleService;
    private final TicketRepository ticketRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(()->new UsernameNotFoundException("Not found user."));
    }

    @Override
    public User registerUser(RegisterUserRequest userRequest) throws IOException, SQLException {
        if(userRepository.existsByEmail(userRequest.getEmail())){
            throw new UserAlreadyExistsException(userRequest.getEmail()+" already exists.");
        }
        User user=new User();
        user.setFullName(userRequest.getFullName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setDob(userRequest.getDob());
        user.formatImageToBlob();
        Role userRole=roleRepository.findByName("ROLE_USER").orElseThrow(()->new FindException("Not found role."));
        user.setRoles(Collections.singletonList(userRole));
        return userRepository.save(user);
    }
    @Override
    public User userUpdateUser(Long id, UserUpdateUserRequest updateUserRequest) throws IOException, SQLException {
        User user=this.getUserById(id);
        if(!StringUtils.isBlank(updateUserRequest.getFullName())){
            user.setFullName(updateUserRequest.getFullName());
        }
        if(updateUserRequest.getDob()!=null){
            user.setDob(updateUserRequest.getDob());
        }
        if(!updateUserRequest.getPhoto().isEmpty() && updateUserRequest.getPhoto()!=null){
            byte[] bytes=updateUserRequest.getPhoto().getBytes();
            Blob blob=new SerialBlob(bytes);
            user.setAvatar(blob);
        }
        return userRepository.save(user);
    }
    @Override
    public User adminUpdateUser(Long id, AdminUpdateUserRequest updateUserRequest) throws IOException, SQLException {
        User user= this.getUserById(id);
        if(!StringUtils.isBlank(updateUserRequest.getFullName())){
            user.setFullName(updateUserRequest.getFullName());
        }
        if(!StringUtils.isBlank(updateUserRequest.getEmail())){
            user.setEmail(updateUserRequest.getEmail());
        }
        if(updateUserRequest.getDob()!=null){
            user.setDob(updateUserRequest.getDob());
        }
        if(!updateUserRequest.getPhoto().isEmpty() && updateUserRequest.getPhoto()!=null){
            byte[] bytes=updateUserRequest.getPhoto().getBytes();
            Blob blob=new SerialBlob(bytes);
            user.setAvatar(blob);
        }
        this.removeAllRoleFromUser(user.getId());
        List<Long> rolesId=updateUserRequest.getRolesId();
        if(rolesId!=null && !rolesId.isEmpty()){
            rolesId.forEach(roleId->roleService.assignRoleToUser(user.getId(),roleId));
        }else{
            Role userRole=roleRepository.findByName("ROLE_USER").orElseThrow(()->new FindException("Not found role."));
            user.getRoles().add(userRole);
        }
        return userRepository.save(user);
    }
    @Override
    public void deleteUser(Long userId) {
        this.removeAllRoleFromUser(userId);
        Optional<User> user= userRepository.findById(userId);
        user.ifPresent(theUser -> userRepository.deleteById(theUser.getId()));
    }

    public void removeAllRoleFromUser(Long userId){
        User user= this.getUserById(userId);
        user.removeAllRoleFromUser();
        userRepository.save(user);
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
        List<Ticket> tickets=ticketRepository.findTicketsByUserId(user.getId());
        return new UserResponse(user.getId()
                ,user.getFullName()
                ,user.getEmail()
                ,user.getPassword()
                ,user.getDob().format(formatDate)
                ,getAvatar(user)
                ,user.getAge()
                , (long) tickets.size()
                ,tickets.stream().mapToDouble(Ticket::getPrice).sum()
                ,user.getRoles());
    }

    @Override
    public UserResponse getUserResponseNonePhoto(User user) {
        DateTimeFormatter formatDate= DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<Ticket> tickets=ticketRepository.findTicketsByUserId(user.getId());
        return new UserResponse(user.getId()
                ,user.getFullName()
                ,user.getEmail()
                ,user.getPassword()
                ,user.getDob().format(formatDate)
                ,null
                ,user.getAge()
                , (long) tickets.size()
                ,tickets.stream().mapToDouble(Ticket::getPrice).sum()
                ,user.getRoles());
    }
}
