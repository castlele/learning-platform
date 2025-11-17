package com.castlelecs.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CourseCreationRequest {
    private String title;
    private String description;
    private Long categoryId;
    private LocalDate startDate;
    private LocalDate dueDate;
}
