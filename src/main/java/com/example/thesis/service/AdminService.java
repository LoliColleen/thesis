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

    // 获取所有未分配的学生
    public List<Student> getUnassignedStudents() {
        return studentRepository.findByPrimaryTeacherIsNull();
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

