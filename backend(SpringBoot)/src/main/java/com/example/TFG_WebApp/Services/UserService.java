package com.example.TFG_WebApp.Services;

import com.example.TFG_WebApp.Models.User;
import com.example.TFG_WebApp.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Page<User> getUsers(Pageable page) {
        return userRepository.findAll(page);
    }

    public User getUser(long idUser) {
        Optional<User> user = userRepository.findById(idUser);
        if (user.isPresent()) {
            return user.get();
        } else {
            return null;
        }
    }

    public User getUserByName(String name) {
        Optional<User> user = userRepository.findByName(name);
        if (user.isPresent()) {
            return user.get();
        } else {
            return null;
        }
    }

    public User addUser(User newUser) {
        try {
            userRepository.save(newUser);
            return newUser;
        } catch (DataIntegrityViolationException e) {
            return null;
        }
    }

    public User modifyUser(long userId, User newUser) {
        User oldUser = getUser(userId);
        if (oldUser != null) {
            try {
                newUser.setId(userId);
                userRepository.save(newUser);
                return newUser;
            } catch (DataIntegrityViolationException e) {
                // if unique restrict is broken, we will return null value to showing this violation of restriction
                return null;
            }
        } else {
            return null;
        }
    }

    public User deleteUser(long userId) {
        User oldUser = getUser(userId);
        if (oldUser != null) {
            userRepository.delete(oldUser);
        }
        return oldUser;
    }
}

