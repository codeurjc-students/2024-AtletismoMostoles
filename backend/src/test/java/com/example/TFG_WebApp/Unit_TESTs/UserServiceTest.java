package com.example.TFG_WebApp.Unit_TESTs;

import com.example.TFG_WebApp.Models.User;
import com.example.TFG_WebApp.Repositories.UserRepository;
import com.example.TFG_WebApp.Services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(new User(), new User()));

        Collection<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void testGetUsers_Pageable() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> usersPage = new PageImpl<>(Collections.singletonList(new User()));

        when(userRepository.findAll(pageable)).thenReturn(usersPage);

        Page<User> result = userService.getUsers(pageable);

        assertEquals(1, result.getTotalElements());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void testGetUser_Success() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUser(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void testGetUser_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        User result = userService.getUser(99L);

        assertNull(result);
        verify(userRepository).findById(99L);
    }

    @Test
    void testGetUserByName_Success() {
        User user = new User();
        user.setName("John");

        when(userRepository.findByName("John")).thenReturn(Optional.of(user));

        User result = userService.getUserByName("John");

        assertNotNull(result);
        assertEquals("John", result.getName());
        verify(userRepository).findByName("John");
    }

    @Test
    void testGetUserByName_NotFound() {
        when(userRepository.findByName("NonExistent")).thenReturn(Optional.empty());

        User result = userService.getUserByName("NonExistent");

        assertNull(result);
        verify(userRepository).findByName("NonExistent");
    }

    @Test
    void testAddUser_Success() {
        User newUser = new User();
        when(userRepository.save(newUser)).thenReturn(newUser);

        User result = userService.addUser(newUser);

        assertNotNull(result);
        verify(userRepository).save(newUser);
    }

    @Test
    void testAddUser_DataIntegrityViolation() {
        User newUser = new User();
        when(userRepository.save(newUser)).thenThrow(DataIntegrityViolationException.class);

        User result = userService.addUser(newUser);

        assertNull(result);
        verify(userRepository).save(newUser);
    }

    @Test
    void testModifyUser_Success() {
        User existingUser = new User();
        existingUser.setId(1L);

        User newUser = new User();
        newUser.setName("Updated");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User result = userService.modifyUser(1L, newUser);

        assertNotNull(result);
        verify(userRepository).save(newUser);
    }

    @Test
    void testModifyUser_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        User newUser = new User();
        User result = userService.modifyUser(99L, newUser);

        assertNull(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testModifyUser_DataIntegrityViolation() {
        User existingUser = new User();
        existingUser.setId(1L);

        User newUser = new User();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        User result = userService.modifyUser(1L, newUser);

        assertNull(result);
        verify(userRepository).save(newUser);
    }

    @Test
    void testDeleteUser_Success() {
        User existingUser = new User();
        existingUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        doNothing().when(userRepository).delete(existingUser);

        User result = userService.deleteUser(1L);

        assertNotNull(result);
        verify(userRepository).delete(existingUser);
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        User result = userService.deleteUser(99L);

        assertNull(result);
        verify(userRepository, never()).delete(any());
    }
}
