package com.example.cinemaserver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Collection<User> users=new HashSet<>();

    public Role(String roleName) {
        this.name=roleName;
    }

    public void assignRoleToUser(User user){
        user.getRoles().add(this);
        this.getUsers().add(user);
    }
    public void removeUserFromRole(User user){
        user.getRoles().remove(this);
        this.getUsers().remove(user);
    }
    public void removeAllUserFromRole(){
        if(this.getUsers()!=null){
            List<User> roleUsers=this.getUsers().stream().toList();
            roleUsers.forEach(this::removeUserFromRole);
        }
    }
}
