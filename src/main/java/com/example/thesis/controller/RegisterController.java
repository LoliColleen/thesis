package com.example.thesis.controller;

import com.example.thesis.entity.User;
import com.example.thesis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    // 显示注册页面
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";  // 返回注册页面
    }

    // 处理注册请求
    @PostMapping("/register")
    public String registerUser(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam("confirmPassword") String confirmPassword,
                               @RequestParam("role") String role,
                               RedirectAttributes redirectAttributes) {

        // 判断密码和确认密码是否一致
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "密码和确认密码不一致");
            return "redirect:/register";
        }

        // 调用用户服务进行注册
        if (userService.register(username, password, role)) {
            return "redirect:/login";  // 注册成功后重定向到登录页面
        } else {
            redirectAttributes.addFlashAttribute("error", "用户名已存在");
            return "redirect:/register";  // 注册失败，返回注册页面
        }
    }
}
