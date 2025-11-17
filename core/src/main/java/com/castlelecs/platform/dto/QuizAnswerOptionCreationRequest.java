package com.castlelecs.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuizAnswerOptionCreationRequest {
    private String text;
    private Boolean correct;
}
