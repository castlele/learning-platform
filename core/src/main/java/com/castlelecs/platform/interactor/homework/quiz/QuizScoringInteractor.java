package com.castlelecs.platform.interactor.homework.quiz;

import com.castlelecs.platform.dto.QuizSubmissionCreationRequest;
import com.castlelecs.platform.entity.Quiz;

public interface QuizScoringInteractor {

    Integer calculateScore(Quiz quiz, QuizSubmissionCreationRequest request);

    boolean isPassed(Quiz quiz, Integer score);
}
