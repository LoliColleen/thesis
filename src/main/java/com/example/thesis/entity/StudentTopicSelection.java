package com.example.thesis.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class StudentTopicSelection {

    @Id
    private Long id;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Topic topic;

}
