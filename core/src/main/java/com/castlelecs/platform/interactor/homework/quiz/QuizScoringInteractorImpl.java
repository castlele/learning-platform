package com.castlelecs.platform.interactor.homework.quiz;

import com.castlelecs.platform.dto.QuizSubmissionCreationRequest;
import com.castlelecs.platform.entity.Quiz;
import com.castlelecs.platform.entity.QuizAnswerOption;
import com.castlelecs.platform.entity.QuizQuestion;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class QuizScoringInteractorImpl implements QuizScoringInteractor {
    @Override
    public Integer calculateScore(Quiz quiz, QuizSubmissionCreationRequest request) {
        if (quiz.getQuestions() == null || quiz.getQuestions().isEmpty()) {
            return 0;
        }

        var answers = request.getAnswers();
        int score = 0;

        for (QuizQuestion question : quiz.getQuestions()) {
            Set<Long> chosenIds = answers.getOrDefault(question.getId(), Set.of());

            Set<Long> correctIds = question.getAnswerOptions().stream()
                    .filter(QuizAnswerOption::isCorrect)
                    .map(QuizAnswerOption::getId)
                    .collect(Collectors.toSet());

            Set<Long> allOptionIds = question.getAnswerOptions().stream()
                    .map(QuizAnswerOption::getId)
                    .collect(Collectors.toSet());

            if (!chosenIds.isEmpty()
                    && allOptionIds.containsAll(chosenIds)
                    && chosenIds.equals(correctIds)) {
                score++;
            }
        }

        return score;
    }

    /**
     * Quiz is passed if score is greater than 60%
     */
    @Override
    public boolean isPassed(Quiz quiz, Integer score) {
        int total = quiz.getQuestions() == null ? 0 : quiz.getQuestions().size();

        if (total == 0) {
            return false;
        }

        int threshold = (int) Math.ceil(total * 0.6);

        return score >= threshold;
    }
}
