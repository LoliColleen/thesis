package com.example.thesis.service;

import com.example.thesis.entity.Student;
import com.example.thesis.entity.Teacher;
import com.example.thesis.repository.StudentRepository;
import com.example.thesis.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private StudentRepository studentRepository;

    // 教师选择学生
    public void selectStudent(Long teacherId, Long studentId) {
        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));

        // 校验教师是否已选满学生
        if (teacher.getStudents().size() >= 5) {
            throw new RuntimeException("Teacher cannot select more students");
        }

        // 校验学生是否已经被选择
        if (student.getPrimaryTeacher() != null) {
            throw new RuntimeException("Student already has a primary teacher");
        }

        teacher.getStudents().add(student);
        student.setPrimaryTeacher(teacher);

        teacherRepository.save(teacher);
        studentRepository.save(student);
    }
}

