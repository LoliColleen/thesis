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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/api/teacher")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private TopicService topicService;
    @Autowired
    private StudentRepository studentRepository;

    // 跳转到教师操作菜单页面
    @GetMapping("/menu")
    public String showMenu() {
        return "teacher/menu";
    }

    // 分页获取教师的题目
    @GetMapping("/{teacherId}/topics")
    public ResponseEntity<Page<Topic>> getTeacherTopics(@PathVariable Long teacherId,
                                                        @RequestParam int page,
                                                        @RequestParam int size) {
        Page<Topic> topics = topicService.getTopicsByTeacherId(teacherId, page, size);
        return ResponseEntity.ok(topics);
    }

    // 提交选择学生
    @PostMapping("/select")
    public String selectStudents(@RequestParam Long teacherId,
                                 @RequestParam List<Long> studentIds,
                                 RedirectAttributes redirectAttributes) {
        try {
            for (Long studentId : studentIds) {
                teacherService.selectStudent(teacherId, studentId);
            }
            redirectAttributes.addFlashAttribute("message", "学生选择成功");
            return "redirect:/api/teacher/select";  // 重定向到教师选择学生页面
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/api/teacher/select";
        }
    }

    @GetMapping("/select")
    public String showStudents(Model model) {
        List<Student> students = studentRepository.findByPrimaryTeacherIsNull();
        model.addAttribute("students", students);
        return "teacher/select"; // 返回教师选择学生页面
    }

    // 教师出题页面
    @GetMapping("/addTopic")
    public String showAddTopicPage(Model model) {
        return "teacher/addTopic";  // 返回前端页面
    }

    // 提交题目
    @PostMapping("/addTopic")
    public String addTopic(@RequestParam Long teacherId,
                           @RequestParam String title,
                           @RequestParam String description,
                           RedirectAttributes redirectAttributes) {
        try {
            topicService.addTopic(teacherId, title, description);
            redirectAttributes.addFlashAttribute("message", "题目添加成功");
            return "redirect:/api/teacher/addTopic";  // 重定向到教师出题页面
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/api/teacher/addTopic";
        }
    }
}
