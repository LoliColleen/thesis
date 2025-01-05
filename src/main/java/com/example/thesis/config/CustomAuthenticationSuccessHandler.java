package com.example.thesis.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 获取当前用户角色
        String role = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .orElse("ROLE_USER");  // 默认角色为 ROLE_USER

        // 根据角色跳转到不同页面
        switch (role) {
            case "ROLE_ADMIN" -> response.sendRedirect("/api/admin/assign");
            case "ROLE_TEACHER" -> response.sendRedirect("/api/teacher/menu");
            case "ROLE_STUDENT" -> response.sendRedirect("/api/student/select");
            default -> response.sendRedirect("/api/student");  // TODO: 默认页面
        }
    }
}