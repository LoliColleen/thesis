package com.example.thesis.service;

import com.example.thesis.entity.Student;
import com.example.thesis.entity.Teacher;
import com.example.thesis.entity.Topic;
import com.example.thesis.repository.StudentRepository;
import com.example.thesis.repository.TeacherRepository;
import com.example.thesis.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private TopicRepository topicRepository;

    // 获取所有未分配的学生
    public List<Student> getUnassignedStudents() {
        return studentRepository.findByPrimaryTeacherIsNull();
    }

    // 分配学生到题目
    public void allocateStudentToTopic(Long studentId, Long topicId) {
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        Optional<Topic> topicOpt = topicRepository.findById(topicId);

        if (studentOpt.isPresent() && topicOpt.isPresent()) {
            Student student = studentOpt.get();
            Topic topic = topicOpt.get();

            // 确保题目没有超过学生选择上限
            student.getSelectedTopics().add(topic);  // 添加学生到题目的选题列表
            student.setPrimaryTeacher(topic.getTeacher());  // 设置教师
            studentRepository.save(student);

            topic.getStudents().add(student);  // 将学生添加到题目的已选学生列表
            topic.setStatus(Topic.Status.SELECTED);  // 设置题目状态为已选
            topicRepository.save(topic);  // 保存题目更新
        } else {
            throw new RuntimeException("学生或题目不存在");
        }
    }

    // 管理员分配学生到教师
    public void allocateStudentToTeacher(Long studentId, Long teacherId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // 校验教师是否已选满学生
        if (teacher.getStudents().size() >= 5) {
            throw new RuntimeException("Teacher cannot take more students");
        }

        student.setPrimaryTeacher(teacher);
        studentRepository.save(student);
    }
}

