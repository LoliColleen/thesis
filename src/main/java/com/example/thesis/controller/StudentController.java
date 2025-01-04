package com.example.thesis.controller;

import com.example.thesis.entity.Student;
import com.example.thesis.entity.Topic;
import com.example.thesis.repository.StudentRepository;
import com.example.thesis.repository.TopicRepository;
import com.example.thesis.service.StudentService;
import com.example.thesis.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
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

    // 学生选题页面
    @GetMapping("/select")
    public String showSelectTopicsPage(Model model) {
        List<Topic> availableTopics = topicService.getAvailableTopics();
        model.addAttribute("topics", availableTopics);
        return "student/select";
    }

    // 提交选题
    @PostMapping("/select")
    public String selectTopics(@RequestParam Long studentId,
                               @RequestParam Set<Long> topicIds,
                               RedirectAttributes redirectAttributes) {
        try {
            studentService.selectTopics(studentId, topicIds);
            redirectAttributes.addFlashAttribute("message", "选题成功");
            return "redirect:/api/student/select";  // 重定向到选题页面
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/api/student/select";
        }
    }
}

