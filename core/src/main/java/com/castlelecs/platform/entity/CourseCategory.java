package com.castlelecs.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "course_categories",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name")
        }
)
@Data
@Builder
public class CourseCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(
            mappedBy = "category",
            cascade = CascadeType.ALL
    )
    @Builder.Default
    private Set<Course> courses = new HashSet<>();
}