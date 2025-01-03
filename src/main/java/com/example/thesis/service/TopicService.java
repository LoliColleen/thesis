package com.example.thesis.service;

import com.example.thesis.entity.Teacher;
import com.example.thesis.entity.Topic;
import com.example.thesis.repository.TeacherRepository;
import com.example.thesis.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    public Topic addTopic(Long teacherId, String title, String description) {
        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Teacher not found"));
        if (teacher.getTopics().size() >= teacher.getMaxTopics()) {
            throw new RuntimeException("Maximum number of topics reached for this teacher");
        }
        Topic topic = new Topic();
        topic.setTitle(title);
        topic.setDescription(description);
        topic.setTeacher(teacher);
        return topicRepository.save(topic);
    }

    // 根据教师ID分页查询题目
    public Page<Topic> getTopicsByTeacherId(Long teacherId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return topicRepository.findByTeacherId(teacherId, pageable);
    }

    // 更新题目状态
    public void updateTopicStatus(Long topicId, Topic.Status status) {
        Topic topic = topicRepository.findById(topicId)
            .orElseThrow(() -> new RuntimeException("Topic not found"));
        topic.setStatus(status);
        topicRepository.save(topic);
    }

    // 获取可选状态的题目
    public Page<Topic> getAvailableTopics() {
        return topicRepository.findByStatus(Topic.Status.AVAILABLE, PageRequest.of(0, 10)); // TODO: 我不确定size应该填多少
    }
}

