package com.example.thesis.controller;

import com.example.thesis.entity.Student;
import com.example.thesis.entity.Teacher;
import com.example.thesis.entity.Topic;
import com.example.thesis.repository.StudentRepository;
import com.example.thesis.repository.TeacherRepository;
import com.example.thesis.repository.TopicRepository;
import com.example.thesis.service.AdminService;
import com.example.thesis.service.StudentService;
import com.example.thesis.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private TopicService topicService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private TopicRepository topicRepository;

    // 提交人工调剂，分配学生到题目
    @PostMapping("/assign")
    public String assignStudentToTopic(@RequestParam Long studentId,
                                       @RequestParam Long topicId,
                                       RedirectAttributes redirectAttributes) {
        try {
            adminService.allocateStudentToTopic(studentId, topicId);  // 修改为分配到题目
            redirectAttributes.addFlashAttribute("message", "学生分配成功");
            return "redirect:/api/admin/assign";  // 重定向到管理员调剂页面
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/api/admin/assign";
        }
    }

    // 显示未分配学生页面
    @GetMapping("/assign")
    public String showUnassignedStudents(Model model) {
        List<Student> students = adminService.getUnassignedStudents();
        List<Topic> topics = topicService.getAvailableTopics();
        model.addAttribute("students", students);
        model.addAttribute("topics", topics);
        return "admin/assign";  // 显示人工调剂页面
    }
}


