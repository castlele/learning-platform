package com.castlelecs.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
public class QuizSubmissionCreationRequest {
    private Map<Long, Set<Long>> answers;
}
