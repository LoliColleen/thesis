package com.example.thesis.service;

import com.example.thesis.entity.Student;
import com.example.thesis.entity.Teacher;
import com.example.thesis.repository.StudentRepository;
import com.example.thesis.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    public void allocateStudentToTeacher(Long studentId, Long teacherId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Teacher not found"));

        if (teacher.getStudents().size() >= teacher.getMaxStudents()) {
            throw new RuntimeException("Teacher cannot take more students");
        }

        student.setPrimaryTeacher(teacher);
        studentRepository.save(student);
    }

    // 获取所有未分配的学生
    public List<Student> getUnassignedStudents() {
        List<Student> allStudents = studentRepository.findAll();
        return allStudents.stream()
            .filter(student -> !student.isAssigned()) // 只保留未分配的学生
            .collect(Collectors.toList());
    }
}

