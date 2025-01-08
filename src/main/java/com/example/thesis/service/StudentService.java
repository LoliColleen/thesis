package com.example.thesis.service;

import com.example.thesis.entity.Student;
import com.example.thesis.entity.Teacher;
import com.example.thesis.entity.Topic;
import com.example.thesis.repository.StudentRepository;
import com.example.thesis.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TopicRepository topicRepository;

    public void selectTopics(Long studentId, Set<Long> topicIds) {
        // 获取学生对象
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));

        // 获取学生已选择的题目
        Set<Topic> selectedTopics = student.getSelectedTopics();

        // 校验学生最多选择2个题目（包括已选和本次选择）
        if (selectedTopics.size() + topicIds.size() > 2) {
            throw new RuntimeException("最多选择两个题目");
        }

        // 校验是否有重复教师的题目
        Set<Teacher> selectedTeachers = new HashSet<>();
        for (Topic selectedTopic : selectedTopics) {
            selectedTeachers.add(selectedTopic.getTeacher());
        }

        Set<Topic> newSelectedTopics = new HashSet<>(selectedTopics); // 不覆盖已有题目

        for (Long topicId : topicIds) {
            Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

            // 校验是否选择了同一位教师的题目
            if (selectedTeachers.contains(topic.getTeacher())) {
                throw new RuntimeException("无法选择同一教师的多个题目");
            }

            // 标记题目为已选
            topic.setStatus(Topic.Status.SELECTED);
            newSelectedTopics.add(topic);
            selectedTeachers.add(topic.getTeacher()); // 更新已选教师列表
        }

        // 更新学生选中的题目
        student.setSelectedTopics(newSelectedTopics);
        studentRepository.save(student);
    }

    public void cancelTopicSelection(Long studentId, Long topicId) {
        // 查找学生和题目
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        Optional<Topic> topicOpt = topicRepository.findById(topicId);

        if (studentOpt.isPresent() && topicOpt.isPresent()) {
            Student student = studentOpt.get();
            Topic topic = topicOpt.get();

            // 从学生的选题列表中移除该题目
            student.getSelectedTopics().remove(topic);

            // 将题目的状态设置为 AVAILABLE
            topic.setStatus(Topic.Status.AVAILABLE);

            // 保存学生和题目的更新
            studentRepository.save(student);
            topicRepository.save(topic);
        } else {
            throw new RuntimeException("无法找到对应的学生或题目");
        }
    }
}

