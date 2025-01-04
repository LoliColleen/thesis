package com.example.thesis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Student {
    @jakarta.persistence.Id
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int maxTopics = 2;

    @ManyToMany
    @JoinTable(
        name = "student_topic_selection", // 中间表名称
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    private Set<Topic> selectedTopics = new HashSet<>();

    @ManyToOne
    private Teacher primaryTeacher;

    // 检查学生是否已被分配
    public boolean isAssigned() {
        return selectedTopics != null && !selectedTopics.isEmpty();
    }
}
