package com.example.TFG_WebApp.RESTControllers;

import com.example.TFG_WebApp.Models.User;
import com.example.TFG_WebApp.Repositories.UserRepository;
import com.example.TFG_WebApp.Services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.Collections;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody @JsonView(User.Basic.class) User user) {

        String pass = user.getEncodedPassword();
        String encoded_pass = passwordEncoder.encode(pass);
        user.setEncodedPassword(encoded_pass);
        user.setRoles(Collections.singletonList("USER"));


        User user1 = userService.addUser(user);

        URI location = fromCurrentRequest().path("/{id}")
                .buildAndExpand(user1.getId()).toUri();

        return ResponseEntity.created(location).body(user1);

    }

    @GetMapping("/me")
    public ResponseEntity<User> me(HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            return ResponseEntity.ok(userRepository.findByName(principal.getName()).orElseThrow());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
