package com.example.thesis.repository;

import com.example.thesis.entity.Student;
import com.example.thesis.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // 通过学生ID查找学生
    Optional<Student> findById(Long id);

    // 查找没有被分配主教师的学生
    List<Student> findByPrimaryTeacherIsNull();

    // 自定义查询：查找某个学生选择的题目
    @Query("SELECT s.selectedTopics FROM Student s WHERE s.id = :studentId")
    Set<Topic> findSelectedTopicsByStudentId(@Param("studentId") Long studentId);
}
