package com.example.thesis.controller;

import com.example.thesis.entity.Student;
import com.example.thesis.entity.Teacher;
import com.example.thesis.entity.Topic;
import com.example.thesis.entity.User;
import com.example.thesis.repository.StudentRepository;
import com.example.thesis.repository.TeacherRepository;
import com.example.thesis.repository.UserRepository;
import com.example.thesis.service.StudentService;
import com.example.thesis.service.TeacherService;
import com.example.thesis.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/menu")
    public String showMenu(Model model) {
        // 获取当前登录教师 ID
        Long teacherId = getCurrentTeacherId();

        // 获取当前教师所出的所有题目
        List<Topic> topics = topicService.getTopicsByTeacherId(teacherId);

        // 获取当前教师的题目及对应的学生
        for (Topic topic : topics) {
            // 获取该题目对应的所有学生，并过滤出 primary teacher 是当前教师的学生
            Set<Student> studentsForTopic = new HashSet<>();
            for (Student student : topic.getStudents()) {
                if (student.getPrimaryTeacher() != null && student.getPrimaryTeacher().getId().equals(teacherId)) {
                    studentsForTopic.add(student);
                }
            }
            topic.setStudents(studentsForTopic);  // 设置过滤后的学生列表
        }

        model.addAttribute("topics", topics);  // 将题目及对应学生传递给前端
        return "teacher/menu";  // 返回教师操作菜单页面
    }

    // 分页获取教师的题目
    @GetMapping("/{teacherId}/topics")
    public ResponseEntity<Page<Topic>> getTeacherTopics(@RequestParam int page,
                                                        @RequestParam int size) {
        var optional = teacherRepository.findByUserId(userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).getId());
        if (optional.isPresent()) {
            Page<Topic> topics = topicService.getTopicsByTeacherId(optional.get().getId(), page, size);
            return ResponseEntity.ok(topics);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 提交选择学生
    @PostMapping("/select")
    public String selectStudents(@RequestParam List<Long> studentIds,
                                 RedirectAttributes redirectAttributes) {
        // 确保 studentIds 不为空
        if (studentIds == null || studentIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "请选择学生");
            return "redirect:/api/teacher/select";
        }

        var optional = teacherRepository.findByUserId(userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).getId());
        if (optional.isPresent()) {
            try {
                for (Long studentId : studentIds) {
                    teacherService.selectStudent(optional.get().getId(), studentId);
                }
                redirectAttributes.addFlashAttribute("message", "学生选择成功");
                return "redirect:/api/teacher/select";  // 重定向到教师选择学生页面
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
                return "redirect:/api/teacher/select";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "无法获取教师");
            return "redirect:/api/teacher/select";
        }
    }

    @GetMapping("/select")
    public String showStudents(Model model) {
        // 获取当前登录教师 ID
        Long teacherId = getCurrentTeacherId();

        // 查询该教师名下的所有学生，且这些学生没有被分配主教师，并且选的 topic 属于当前教师
        List<Student> students = studentRepository.findByPrimaryTeacherIsNullAndSelectedTopicsTeacherId(teacherId);

        // 打印调试信息，确保查询结果不为空
        System.out.println("查询的学生数量: " + students.size());

        model.addAttribute("students", students);
        return "teacher/select"; // 返回教师选择学生页面
    }

    // 获取当前登录教师的 ID
    private Long getCurrentTeacherId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Teacher> teacher = teacherRepository.findByUserId(userRepository.findByUsername(username).getId());
        return teacher.map(Teacher::getId).orElse(null);
    }

    // 教师出题页面
    @GetMapping("/addTopic")
    public String showAddTopicPage(Model model) {
        return "teacher/addTopic";  // 返回前端页面
    }

    // 提交题目
    @PostMapping("/addTopic")
    public String addTopic(@RequestParam String title,
                           @RequestParam String description,
                           RedirectAttributes redirectAttributes) {
        var optional = teacherRepository.findByUserId(userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).getId());
        if (optional.isPresent()) {
            try {
                topicService.addTopic(optional.get().getId(), title, description);
                redirectAttributes.addFlashAttribute("message", "题目添加成功");
                return "redirect:/api/teacher/addTopic";  // 重定向到教师出题页面
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
                return "redirect:/api/teacher/addTopic";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "无法获取教师");
            return "redirect:/api/teacher/select";
        }
    }
}
