package com.example.thesis.controller;

import com.example.thesis.entity.Student;
import com.example.thesis.entity.Topic;
import com.example.thesis.repository.StudentRepository;
import com.example.thesis.repository.TopicRepository;
import com.example.thesis.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/select")
    public String showTopics(Model model) {
        List<Topic> topics = topicRepository.findAll();
        model.addAttribute("topics", topics);
        return "student"; // 返回学生选题页面
    }

    @PostMapping("/select")
    public String selectTopics(Long studentId, List<Long> topicIds) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Topic> selectedTopics = topicRepository.findAllById(topicIds);
        student.setSelectedTopics(new HashSet<>(selectedTopics));
        studentRepository.save(student);

        return "redirect:/student/select"; // 重定向到选题页面
    }
}

