package com.example.service1.Security;

import com.example.service1.Models.User;
import com.example.service1.Repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("securityDataInitializer")
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${security.admin}")
    private String admin;

    @Value("${security.adminPasword}")
    private String adminPasword;

    @Value("${security.user1}")
    private String user1;

    @Value("${security.password1}")
    private String user1Pasword;

    @PostConstruct
    public void initDatabase() {

        if (userRepository.findByName(admin).isEmpty()) {
            User user = new User();
            user.setName(admin);
            user.setEncodedPassword(passwordEncoder.encode(adminPasword));
            user.setRoles(List.of("ADMIN"));
            userRepository.save(user);
        }

        if (userRepository.findByName(user1).isEmpty()) {
            User user = new User();
            user.setName(user1);
            user.setEncodedPassword(passwordEncoder.encode(user1Pasword));
            user.setRoles(List.of("USER"));
            userRepository.save(user);
        }
    }

}
