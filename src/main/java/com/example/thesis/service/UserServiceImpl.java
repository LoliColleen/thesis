package com.example.thesis.service;

import com.example.thesis.entity.Student;
import com.example.thesis.entity.Teacher;
import com.example.thesis.repository.StudentRepository;
import com.example.thesis.repository.TeacherRepository;
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
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;
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
        com.example.thesis.entity.User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("用户未找到");
        }

        // 返回 Spring Security 的 UserDetails 实现类
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())  // 密码从数据库获取，通常需要加密存储
            .roles(user.getRole().replace("ROLE_", ""))  // 移除 "ROLE_" 前缀并设置角色
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
        user.setRole("ROLE_" + role.toUpperCase());

        // 先保存 User 实体，确保 User 有一个 ID
        userRepository.save(user);  // 保存用户

        // 根据角色创建对应的实体
        switch (role.toUpperCase()) {
            case "STUDENT":
                // 创建学生并关联用户
                Student student = new Student();
                student.setUser(user);  // 假设 Student 与 User 有一对一关系
                studentRepository.save(student);  // 保存学生信息
                break;
            case "TEACHER":
                // 创建教师并关联用户
                Teacher teacher = new Teacher();
                teacher.setUser(user);  // 假设 Teacher 与 User 有一对一关系
                teacherRepository.save(teacher);  // 保存教师信息
                break;
            case "ADMIN":
                // 不需要额外创建 Admin 实体
                break;
            default:
                return false;  // 如果角色不正确，返回 false
        }

        return true;
    }

}
