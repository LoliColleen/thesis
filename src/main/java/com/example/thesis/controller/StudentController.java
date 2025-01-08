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

import java.util.ArrayList;
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
        var student = studentRepository.findByUserId(userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).getId()).orElse(null);

        if (student != null) {
            // 如果学生已经有主导师，显示已选题目并禁止修改
            if (student.getPrimaryTeacher() != null) {
                Set<Topic> selectedTopics = student.getSelectedTopics();

                // 过滤出主导师发布的题目
                Set<Topic> primaryTeacherTopics = new HashSet<>();
                for (Topic topic : selectedTopics) {
                    if (topic.getTeacher().equals(student.getPrimaryTeacher())) {
                        primaryTeacherTopics.add(topic);
                    }
                }

                model.addAttribute("selectedTopics", primaryTeacherTopics);
                model.addAttribute("primaryTeacher", student.getPrimaryTeacher());
                return "student/select";  // 不允许学生修改选题，直接返回
            }

            // 获取学生已经选择的题目
            Set<Topic> selectedTopics = student.getSelectedTopics();

            // 确保 selectedTopics 是可修改的
            Set<Topic> modifiableSelectedTopics = new HashSet<>(selectedTopics);  // 创建一个可修改的集合

            // 获取可选的题目（排除已经选择的同一教师的题目）
            Set<Long> selectedTeacherIds = new HashSet<>();
            for (Topic topic : modifiableSelectedTopics) {
                selectedTeacherIds.add(topic.getTeacher().getId());  // 记录已经选择过的教师ID
            }

            // 获取所有可以选择的题目（不是当前教师的题目且学生还没选过）
            List<Topic> availableTopics = new ArrayList<>(topicService.getAllTopics());
            availableTopics.removeIf(topic -> selectedTeacherIds.contains(topic.getTeacher().getId()));

            model.addAttribute("selectedTopics", modifiableSelectedTopics);
            model.addAttribute("availableTopics", availableTopics);
        }

        return "student/select";
    }

    // 提交选题
    @PostMapping("/select")
    public String selectTopics(@RequestParam Set<Long> topicIds, RedirectAttributes redirectAttributes) {
        var studentOpt = studentRepository.findByUserId(
            userRepository.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName()
            ).getId()
        );
        if (studentOpt.isPresent()) {
            try {
                studentService.selectTopics(studentOpt.get().getId(), topicIds);
                redirectAttributes.addFlashAttribute("message", "选题成功");
                return "redirect:/api/student/select";  // 重定向到选题页面
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
                return "redirect:/api/student/select";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "无法获取学生");
            return "redirect:/api/student/select";
        }
    }

    // 取消选题
    @PostMapping("/select/cancel")
    public String cancelTopicSelection(@RequestParam Long topicId, RedirectAttributes redirectAttributes) {
        var studentOpt = studentRepository.findByUserId(userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).getId());
        if (studentOpt.isPresent()) {
            try {
                studentService.cancelTopicSelection(studentOpt.get().getId(), topicId);
                redirectAttributes.addFlashAttribute("message", "取消选题成功");
                System.out.printf("取消选题成功");
                return "redirect:/api/student/select";  // 重定向到选题页面
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
                System.out.printf(e.getMessage());
                return "redirect:/api/student/select";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "无法获取学生");
            return "redirect:/api/student/select";
        }
    }
}

