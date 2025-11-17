package com.castlelecs.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AssignmentCreationRequest {
    private String title;
    private String description;
    private LocalDate dueDate;
    private Integer maxScore;
}
