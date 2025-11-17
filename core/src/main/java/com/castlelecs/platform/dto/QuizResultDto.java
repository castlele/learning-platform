package com.castlelecs.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuizResultDto {
    private Long quizId;
    private Long studentId;
    private Integer score;
    private Boolean passed;
}
