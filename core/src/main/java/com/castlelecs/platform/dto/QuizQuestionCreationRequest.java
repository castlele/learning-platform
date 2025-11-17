package com.castlelecs.platform.dto;

import com.castlelecs.platform.entity.QuizQuestionType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuizQuestionCreationRequest {
    private String text;
    private QuizQuestionType type;
}
