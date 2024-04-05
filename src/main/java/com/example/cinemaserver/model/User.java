package com.example.cinemaserver.model;

import jakarta.persistence.*;
import lombok.Data;
import org.apache.commons.io.IOUtils;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private LocalDate dob;
    @Lob
    private Blob avatar;
    @Transient
    private Integer age;
    @ManyToMany(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH})
    @JoinTable(name="user_role",
        joinColumns = @JoinColumn(name = "Userid",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "Roleid",referencedColumnName = "id")
    )
    private Collection<Role> roles=new HashSet<>();
    public Integer getAge() {
        return Period.between(this.dob,LocalDate.now()).getYears();
    }
    public void removeRoleFromUser(Role role){
        role.getUsers().remove(this);
        this.getRoles().remove(role);
    }

    public void removeAllRoleFromUser(){
        if(this.getRoles()!=null){
            List<Role> roles=this.getRoles().stream().toList();
            roles.forEach(this::removeRoleFromUser);
        }
    }

    public void formatImageToBlob() throws IOException {
        URL url = new URL("https://st.quantrimang.com/photos/image/2017/04/08/anh-dai-dien-FB-200.jpg");
        try (InputStream inputStream = url.openStream()) {
            byte[] imageBytes = IOUtils.toByteArray(inputStream);
            this.avatar= new SerialBlob(imageBytes);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
