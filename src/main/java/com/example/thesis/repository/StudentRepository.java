package com.example.thesis.repository;

import com.example.thesis.entity.Student;
import com.example.thesis.entity.Teacher;
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

    // 通过用户ID查找
    Optional<Student> findByUserId(Long id);

    // 查找没有被分配主教师的学生
    List<Student> findByPrimaryTeacherIsNull();

    // 根据 Topic id 查找所有相关的学生
    @Query("SELECT s FROM Student s JOIN s.selectedTopics t WHERE t.id = :topicId")
    List<Student> findStudentsByTopicId(@Param("topicId") Long topicId);

    // 查询所有学生，带上他们所选择的 Topic
    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.selectedTopics")
    List<Student> findAllStudentsWithTopics();

    // 查询所有学生，并确保选中的 Topic 属于当前教师
    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.selectedTopics st LEFT JOIN FETCH st.teacher t WHERE t.id = :teacherId")
    List<Student> findAllStudentsWithTopicsByTeacher(@Param("teacherId") Long teacherId);

    // 查询没有分配主教师的学生，并且这些学生选的 Topic 属于当前教师
    @Query("SELECT s FROM Student s JOIN s.selectedTopics t WHERE s.primaryTeacher IS NULL AND t.teacher.id = :teacherId")
    List<Student> findByPrimaryTeacherIsNullAndSelectedTopicsTeacherId(@Param("teacherId") Long teacherId);

    // 自定义查询：查找某个学生选择的题目
    @Query("SELECT s.selectedTopics FROM Student s WHERE s.id = :studentId")
    Set<Topic> findSelectedTopicsByStudentId(@Param("studentId") Long studentId);
}
