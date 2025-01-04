package com.example.thesis.controller;

import com.example.thesis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    // 显示登录页面
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";  // 返回 login.html 或 login.jsp 页面
    }

    // 处理登录请求
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        RedirectAttributes redirectAttributes) {

        if (userService.validateUser(username, password)) {
            // 登录成功，获取用户的 UserDetails
            UserDetails userDetails = userService.loadUserByUsername(username);
            System.out.println("User logged in: " + username);

            // 构造一个认证对象
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, password,
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))); // 可以根据数据库角色信息进一步优化
            System.out.println("User logged in: " + username);

            // 将认证对象存储到 SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return "redirect:/api/student";  // TODO: 登录成功后重定向
        } else {
            // 登录失败，返回登录页并显示错误消息
            System.out.println("Invalid username or password");
            redirectAttributes.addFlashAttribute("error", "用户名或密码错误");
            return "redirect:/login";  // 登录失败后重定向回登录页
        }
    }
}
