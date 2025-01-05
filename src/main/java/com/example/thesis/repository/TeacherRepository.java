package com.example.thesis.repository;

import com.example.thesis.entity.Student;
import com.example.thesis.entity.Teacher;
import com.example.thesis.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    // 通过教师ID查找教师
    Optional<Teacher> findById(Long id);

    // 通过教师ID查找教师
    Optional<Teacher> findByUserId(Long id);

    // 查找某个教师发布的题目
    @Query("SELECT t.topics FROM Teacher t WHERE t.id = :teacherId")
    Set<Topic> findTopicsByTeacherId(@Param("teacherId") Long teacherId);

    // 查找某个教师的学生
    @Query("SELECT t.students FROM Teacher t WHERE t.id = :teacherId")
    Set<Student> findStudentsByTeacherId(@Param("teacherId") Long teacherId);
}
