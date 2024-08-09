package com.example.cinemaserver.config;

import com.example.cinemaserver.model.Area;
import com.example.cinemaserver.model.Role;
import com.example.cinemaserver.model.User;
import com.example.cinemaserver.repository.AreaRepository;
import com.example.cinemaserver.repository.RoleRepository;
import com.example.cinemaserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private static final String[] LIST_AREA={
            "Hà Nội", "Hưng Yên", "Đà Nẵng", "Hồ Chí Minh"
    };

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository
            , RoleRepository roleRepository
            , PasswordEncoder passwordEncoder
            , AreaRepository areaRepository){
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

            List<String> areas=Arrays.asList(LIST_AREA);
            for(String areaName:areas){
                if(!areaRepository.existsByName(areaName)){
                    Area area=new Area(areaName);
                    areaRepository.save(area);
                }
            }
        };
    }
}
