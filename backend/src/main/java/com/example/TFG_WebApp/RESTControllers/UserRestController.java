package com.example.TFG_WebApp.RESTControllers;

import com.example.TFG_WebApp.Models.User;
import com.example.TFG_WebApp.Repositories.UserRepository;
import com.example.TFG_WebApp.Services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

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
    public ResponseEntity<?> register(@RequestBody @JsonView(User.Basic.class) User user) {
        try {
            if (userRepository.findByName(user.getName()).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El nombre de usuario ya existe"));
            }
            String rawPassword = user.getRawPassword();
            if (rawPassword == null || rawPassword.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "La contraseña no puede estar vacía"));
            }

            String encodedPassword = passwordEncoder.encode(rawPassword);
            user.setEncodedPassword(encodedPassword);

            user.setRoles(Collections.singletonList("USER"));

            User userSaved = userService.addUser(user);
            if (userSaved == null) {
                return ResponseEntity.internalServerError().body(Map.of("error", "No se pudo crear el usuario"));
            }
            URI location = fromCurrentRequest().path("/{id}")
                    .buildAndExpand(userSaved.getId()).toUri();

            return ResponseEntity.created(location).body(userSaved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autenticado"));
        }

        Optional<User> user = userRepository.findByName(userDetails.getUsername());

        if (user.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
        }

        User currentUser = user.get();

        return ResponseEntity.ok(Map.of(
                "username", currentUser.getName(),
                "roles", currentUser.getRoles()
        ));
    }
}
