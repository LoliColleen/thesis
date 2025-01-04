package com.example.thesis.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    boolean validateUser(String username, String password);
    UserDetails loadUserByUsername(String username);
    boolean register(String username, String password, String role);
}
