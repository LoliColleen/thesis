package com.example.thesis.controller;

import com.example.thesis.entity.Student;
import com.example.thesis.entity.Topic;
import com.example.thesis.repository.StudentRepository;
import com.example.thesis.service.StudentService;
import com.example.thesis.service.TeacherService;
import com.example.thesis.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private TopicService topicService;
    @Autowired
    private StudentRepository studentRepository;

    // 分页获取教师的题目
    @GetMapping("/{teacherId}/topics")
    public ResponseEntity<Page<Topic>> getTeacherTopics(@PathVariable Long teacherId,
                                                        @RequestParam int page,
                                                        @RequestParam int size) {
        Page<Topic> topics = topicService.getTopicsByTeacherId(teacherId, page, size);
        return ResponseEntity.ok(topics);
    }

    // 教师选择学生
    @PostMapping("/select-student")
    public ResponseEntity<String> selectStudent(@RequestParam Long teacherId, @RequestParam Long studentId) {
        try {
            teacherService.selectStudent(teacherId, studentId);
            return ResponseEntity.ok("Student selected successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/select")
    public String showStudents(Model model) {
        List<Student> students = studentRepository.findAll();
        model.addAttribute("students", students);
        return "teacher"; // 返回教师选择学生页面
    }
}