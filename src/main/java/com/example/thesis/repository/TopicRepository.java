package com.example.thesis.repository;

import com.example.thesis.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    // 根据教师ID查询题目并支持分页
    Page<Topic> findByTeacherId(Long teacherId, Pageable pageable);

    // 根据教师ID查询题目并支持分页
    List<Topic> findByTeacherId(Long teacherId);

    // 根据题目状态查询题目
    Page<Topic> findByStatus(Topic.Status status, Pageable pageable);

    // 查找教师和状态的组合
    Page<Topic> findByTeacherIdAndStatus(Long teacherId, Topic.Status status, Pageable pageable);

    // 查找某学生选择的题目
    @Query("SELECT t FROM Topic t WHERE t.id IN (SELECT st.topic.id FROM StudentTopicSelection st WHERE st.student.id = :studentId)")
    List<Topic> findSelectedTopicsByStudentId(@Param("studentId") Long studentId);
}
