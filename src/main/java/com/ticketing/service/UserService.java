package com.ticketing.service;

import com.ticketing.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User registerUser(User user);
    User findByUsername(String username);
    User findByEmail(String email);
    User findById(Long id);
    User updateUser(User user);
    boolean changePassword(Long userId, String currentPassword, String newPassword);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findAllUsers();
    long getUserReservationCount(Long userId);
}
