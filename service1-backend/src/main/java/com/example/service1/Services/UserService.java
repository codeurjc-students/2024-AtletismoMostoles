package com.example.service1.Services;

import com.example.service1.Entities.User;
import com.example.service1.GrpcClients.EventGrpcClient;
import com.example.service1.Repositories.UserRepository;
import com.example.service3.grpc.EventoServiceGrpcProto.NotificationData;
import com.example.service3.grpc.EventoServiceGrpcProto.NotificationsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EventGrpcClient eventGrpcClient;

    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Page<User> getUsers(Pageable page) {
        return userRepository.findAll(page);
    }

    public User getUser(long idUser) {
        Optional<User> user = userRepository.findById(idUser);
        return user.orElse(null);
    }

    public User getUserByName(String name) {
        Optional<User> user = userRepository.findByName(name);
        return user.orElse(null);
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

    public void updateLastLogin(User user) {
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    public List<NotificationData> getPendingNotifications(User user, LocalDateTime lastLogin) {
        if (user == null || lastLogin == null) {
            return List.of();
        }
        System.out.println("Service Timestamp: " + lastLogin );
        NotificationsResponse response = eventGrpcClient.pendingNotifications(user.getId(), lastLogin.toString());
        return response.getNotificacionesList();
    }


}