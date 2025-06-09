package com.example.TFG_WebApp.RESTControllers;

import com.example.TFG_WebApp.Models.User;
import com.example.TFG_WebApp.Models.UserDTO;
import com.example.TFG_WebApp.Services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {


    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public Page<UserDTO> seeUsers(Pageable page) {
        Page<User> users = userService.getUsers(page);


        // Convert Page<User> result to Page<UserDTO>
        return users.map(user -> {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            return userDTO;
        });
    }

    @GetMapping("/user/{idUser}")
    public ResponseEntity<User> seeUser(@PathVariable long idUser) {

        User user = userService.getUser(idUser);
        if (user != null) {
            return ResponseEntity.ok(user);

        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody @JsonView(User.ViewUsers.class) User user) {

        String pass = user.getEncodedPassword();
        String encoded_pass = passwordEncoder.encode(pass);
        user.setEncodedPassword(encoded_pass);

        User user1 = userService.addUser(user);

        URI location = fromCurrentRequest().path("/{id}")
                .buildAndExpand(user1.getId()).toUri();

        return ResponseEntity.created(location).body(user1);

    }

    @PutMapping("/user/{idUser}")
    public ResponseEntity<User> modifyUser(@PathVariable long idUser, @RequestBody @JsonView(User.ViewUsers.class) User newUser) {


        User user = userService.getUser(idUser);
        if (user != null) {

            newUser.setId(idUser);
            String pass = newUser.getEncodedPassword();
            String encoded_pass = passwordEncoder.encode(pass);
            newUser.setEncodedPassword(encoded_pass);
            userService.addUser(newUser);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @DeleteMapping("/user/{idUser}")
    public ResponseEntity<User> deleteUser(@PathVariable long idUser) {


        User user = userService.getUser(idUser);
        if (user != null) {
            userService.deleteUser(idUser);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
