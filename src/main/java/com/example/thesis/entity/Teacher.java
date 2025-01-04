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
public class Teacher {
    @jakarta.persistence.Id
    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int maxTopics = 5;

    @Column(nullable = false)
    private int maxStudents = 5;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private Set<Topic> topics = new HashSet<>();

    @OneToMany(mappedBy = "primaryTeacher", cascade = CascadeType.ALL)
    private Set<Student> students = new HashSet<>();

    // Getters and Setters
}
