package com.example.thesis.service;

import com.example.thesis.entity.Role;
import com.example.thesis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;  // 假设你有一个 UserRepository 用于数据库操作
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public boolean validateUser(String username, String password) {
        // 校验用户是否存在以及密码是否匹配
        UserDetails userDetails = loadUserByUsername(username);
        return userDetails != null && userDetails.getPassword().equals(password);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库中获取用户信息
        // 假设你有一个 User 类来映射数据库中的用户
        com.example.thesis.entity.User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("用户未找到");
        }

        // 返回 Spring Security 的 UserDetails 实现类
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())  // 密码从数据库获取，通常需要加密存储
            .roles(user.getRole().name())  // 角色信息
            .build();
    }

    // 注册用户
    public boolean register(String username, String password, String role) {
        if (userRepository.existsByUsername(username)) {
            return false;  // 用户名已存在
        }

        // 创建新用户并保存
        com.example.thesis.entity.User user = new com.example.thesis.entity.User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        // 设置角色
        switch (role.toUpperCase()) {
            case "STUDENT":
                user.setRole(Role.STUDENT);
                break;
            case "TEACHER":
                user.setRole(Role.TEACHER);
                break;
            case "ADMIN":
                user.setRole(Role.ADMIN);
                break;
            default:
                user.setRole(Role.STUDENT);  // 默认设置为学生
        }

        userRepository.save(user);  // 保存用户
        return true;
    }
}
