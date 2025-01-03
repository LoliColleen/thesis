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
public class Topic {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String description;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Teacher teacher;

    @Enumerated(EnumType.STRING)
    private Status status = Status.AVAILABLE;

    @ManyToMany(mappedBy = "selectedTopics")
    private Set<Student> students = new HashSet<>();

    public enum Status {
        AVAILABLE, // 可选
        SELECTED, // 已选
        ASSIGNED  // 已分配
    }
}
