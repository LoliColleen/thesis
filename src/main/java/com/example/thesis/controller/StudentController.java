package com.example.thesis.controller;

import com.example.thesis.entity.Student;
import com.example.thesis.entity.Topic;
import com.example.thesis.repository.StudentRepository;
import com.example.thesis.repository.TeacherRepository;
import com.example.thesis.repository.TopicRepository;
import com.example.thesis.repository.UserRepository;
import com.example.thesis.service.StudentService;
import com.example.thesis.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/api/student")
public class StudentController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private TopicService topicService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private UserRepository userRepository;

    // 学生选题页面
    @GetMapping("/select")
    public String showSelectTopicsPage(Model model) {
        List<Topic> availableTopics = topicService.getAvailableTopics();
        model.addAttribute("topics", availableTopics);
        return "student/select";
    }

    // 提交选题
    @PostMapping("/select")
    public String selectTopics(
        @RequestParam Set<Long> topicIds,
                               RedirectAttributes redirectAttributes) {
        var optional = studentRepository.findByUserId(userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).getId());
        if (optional.isPresent()) {
            try {
                studentService.selectTopics(optional.get().getId(), topicIds);
                redirectAttributes.addFlashAttribute("message", "选题成功");
                return "redirect:/api/student/select";  // 重定向到选题页面
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
                return "redirect:/api/student/select";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "无法获取学生");
            return "redirect:/api/teacher/select";
        }
    }
}

