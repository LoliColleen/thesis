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

    // 显示未分配学生页面
    @GetMapping("/assign")
    public String showUnassignedStudents(Model model) {
        List<Student> students = adminService.getUnassignedStudents();
        List<Teacher> teachers = teacherRepository.findAll();
        model.addAttribute("students", students);
        model.addAttribute("teachers", teachers);
        return "admin/assign";  // 显示人工调剂页面
    }

    // 提交人工调剂
    @PostMapping("/assign")
    public String assignStudentToTeacher(@RequestParam Long studentId,
                                         @RequestParam Long teacherId,
                                         RedirectAttributes redirectAttributes) {
        try {
            adminService.allocateStudentToTeacher(studentId, teacherId);
            redirectAttributes.addFlashAttribute("message", "学生分配成功");
            return "redirect:/api/admin/assign";  // 重定向到管理员调剂页面
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/api/admin/assign";
        }
    }
}


