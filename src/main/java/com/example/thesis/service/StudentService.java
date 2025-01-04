package com.example.thesis.service;

import com.example.thesis.entity.Student;
import com.example.thesis.entity.Topic;
import com.example.thesis.repository.StudentRepository;
import com.example.thesis.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TopicRepository topicRepository;

    // 学生选择题目
    public void selectTopics(Long studentId, Set<Long> topicIds) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));

        // 校验学生最多选择2个题目
        if (topicIds.size() > 2) {
            throw new RuntimeException("Cannot select more than 2 topics");
        }

        Set<Topic> selectedTopics = new HashSet<>();
        for (Long topicId : topicIds) {
            Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

            // 校验是否有重复教师的题目
            if (selectedTopics.stream().anyMatch(t -> t.getTeacher().equals(topic.getTeacher()))) {
                throw new RuntimeException("Cannot select multiple topics from the same teacher");
            }

            if (topic.getStatus() == Topic.Status.SELECTED) {
                throw new RuntimeException("Topic already selected");
            }

            topic.setStatus(Topic.Status.SELECTED);
            selectedTopics.add(topic);
        }

        student.setSelectedTopics(selectedTopics);
        studentRepository.save(student);
    }
}

