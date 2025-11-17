package com.castlelecs.platform.service.homework;

import com.castlelecs.platform.entity.Module;
import com.castlelecs.platform.dto.*;
import com.castlelecs.platform.entity.*;
import com.castlelecs.platform.repository.*;
import com.castlelecs.platform.service.course.CourseService;
import com.castlelecs.platform.service.course.ModuleService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class QuizSubmissionServiceTest {
    private static final String PROFESSOR_EMAIL = "prof@platform.test";
    private static final String PROFESSOR_FIRST_NAME = "Prof";
    private static final String PROFESSOR_LAST_NAME = "Essor";

    private static final String STUDENT_EMAIL = "student@platform.test";
    private static final String STUDENT_FIRST_NAME = "Stud";
    private static final String STUDENT_LAST_NAME = "Ent";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuizAnswerOptionRepository quizAnswerOptionRepository;

    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizSubmissionService sut;

    private Long professorId;
    private Long studentId;
    private Long quizId;
    private Long questionId;
    private Long correctOptionId;
    private Long wrongOptionId;

    @BeforeEach
    public void setUp() {
        Profile professorProfile = Profile.builder()
                .email(PROFESSOR_EMAIL)
                .firstName(PROFESSOR_FIRST_NAME)
                .lastName(PROFESSOR_LAST_NAME)
                .build();

        User professor = User.builder()
                .role(UserRole.PROFESSOR)
                .profile(professorProfile)
                .build();

        professorProfile.setUser(professor);
        userRepository.save(professor);
        this.professorId = professor.getId();

        Profile studentProfile = Profile.builder()
                .email(STUDENT_EMAIL)
                .firstName(STUDENT_FIRST_NAME)
                .lastName(STUDENT_LAST_NAME)
                .build();

        User student = User.builder()
                .role(UserRole.STUDENT)
                .profile(studentProfile)
                .build();

        studentProfile.setUser(student);
        userRepository.save(student);
        this.studentId = student.getId();

        CourseCreationRequest courseRequest = new CourseCreationRequest(
                "ORM",
                "Nothing here",
                null,
                LocalDate.now(),
                LocalDate.now()
        );
        Course course = courseService.createCourse(professorId, courseRequest);

        ModuleCommand moduleRequest = new ModuleCommand(
                "Module 1",
                "Intro",
                1
        );
        Module module = moduleService.createModule(course.getId(), moduleRequest);

        QuizCreationRequest quizRequest = new QuizCreationRequest("Quiz 1");
        Quiz quiz = quizService.createQuiz(professorId, module.getId(), quizRequest);
        this.quizId = quiz.getId();

        QuizQuestionCreationRequest questionRequest = new QuizQuestionCreationRequest(
                "What is ORM?",
                QuizQuestionType.SINGLE_CHOICE
        );

        QuizAnswerOptionCreationRequest opt1 = new QuizAnswerOptionCreationRequest(
                "Object Relational Mapping",
                true
        );
        QuizAnswerOptionCreationRequest opt2 = new QuizAnswerOptionCreationRequest(
                "Only Relational Model",
                false
        );

        QuizQuestion question = quizService.addQuestion(
                quizId,
                questionRequest,
                Set.of(opt1, opt2)
        );

        this.questionId = question.getId();

        List<QuizAnswerOption> options = quizAnswerOptionRepository.findAll();
        QuizAnswerOption correct = options.stream().filter(QuizAnswerOption::isCorrect).findFirst().orElseThrow();
        QuizAnswerOption wrong = options.stream().filter(o -> !o.isCorrect()).findFirst().orElseThrow();
        this.correctOptionId = correct.getId();
        this.wrongOptionId = wrong.getId();
    }

    @Test
    public void testSubmitQuizWithWrongAnswer() {
        QuizSubmissionCreationRequest request = new QuizSubmissionCreationRequest(
                Map.of(questionId, Set.of(wrongOptionId))
        );

        QuizResultDto result = sut.submitQuiz(studentId, quizId, request);

        assertEquals(0, result.getScore());
        assertFalse(result.getPassed());
    }

    @Test
    public void testGetResultsForStudentAndQuiz() {
        QuizSubmissionCreationRequest request = new QuizSubmissionCreationRequest(
                Map.of(questionId, Set.of(correctOptionId))
        );
        sut.submitQuiz(studentId, quizId, request);

        List<QuizSubmission> byStudent = sut.getResultsForStudent(studentId);
        assertEquals(1, byStudent.size());
        assertEquals(quizId, byStudent.getFirst().getQuiz().getId());

        List<QuizSubmission> byQuiz = sut.getResultsForQuiz(quizId);
        assertEquals(1, byQuiz.size());
        assertEquals(studentId, byQuiz.getFirst().getStudent().getId());
    }
}
