package com.example.thesis.controller;

import com.example.thesis.entity.Student;
import com.example.thesis.entity.Teacher;
import com.example.thesis.repository.StudentRepository;
import com.example.thesis.repository.TeacherRepository;
import com.example.thesis.service.AdminService;
import com.example.thesis.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    // 分配学生给教师
    @PostMapping("/allocate-student")
    public ResponseEntity<String> allocateStudentToTeacher(@RequestParam Long studentId, @RequestParam Long teacherId) {
        try {
            adminService.allocateStudentToTeacher(studentId, teacherId);
            return ResponseEntity.ok("Student allocated to teacher successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 获取未分配教师的学生列表
    @GetMapping("/unassigned-students")
    public ResponseEntity<List<Student>> getUnassignedStudents() {
        List<Student> unassignedStudents = adminService.getUnassignedStudents();
        return ResponseEntity.ok(unassignedStudents);
    }

    @GetMapping("/assign")
    public String assignStudents(Model model) {
        List<Student> students = adminService.getUnassignedStudents();
        List<Teacher> teachers = teacherRepository.findAll();

        model.addAttribute("students", students);
        model.addAttribute("teachers", teachers); // 添加教师列表

        return "admin"; // 返回管理员调剂页面
    }
}


