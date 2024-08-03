package com.example.cinemaserver.config;

import com.example.cinemaserver.model.Role;
import com.example.cinemaserver.model.User;
import com.example.cinemaserver.repository.RoleRepository;
import com.example.cinemaserver.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.text.html.parser.Entity;
import java.lang.module.FindException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {
    private static final String[] LIST_ROLE={
            "ROLE_USER","ROLE_ADMIN"
    };

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository
            , RoleRepository roleRepository
            , PasswordEncoder passwordEncoder){
        log.info("Initializing application...");
        return args -> {
            List<String> roles= Arrays.asList(LIST_ROLE);

            for(String roleName:roles){
                if(roleRepository.findByName(roleName).isEmpty()){
                    Role role=new Role(roleName);
                    roleRepository.save(role);
                }
            }

            if(userRepository.findByEmail("nnmhqn2003@gmail.com").isEmpty()){
                User user=new User();
                user.setFullName("Nguyễn Minh Quân");
                user.setEmail("nnmhqn2003@gmail.com");
                user.setPassword(passwordEncoder.encode("1"));
                user.setDob(LocalDate.of(2003,8,21));
                user.formatImageToBlob();

                Role roleADMIN=roleRepository.findByName("ROLE_ADMIN").orElseThrow(()->new FindException("Not found role."));
                user.setRoles(Collections.singletonList(roleADMIN));
                userRepository.save(user);
            }
        };
    }
}
