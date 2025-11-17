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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class QuizServiceTest {
    private static final String PROFESSOR_EMAIL = "prof@platform.test";
    private static final String PROFESSOR_FIRST_NAME = "Prof";
    private static final String PROFESSOR_LAST_NAME = "Essor";

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
    private CourseService courseService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private QuizService sut;

    private Long professorId;
    private Long moduleId;

    @BeforeEach
    public void setUp() {
        Profile profile = Profile.builder()
                .email(PROFESSOR_EMAIL)
                .firstName(PROFESSOR_FIRST_NAME)
                .lastName(PROFESSOR_LAST_NAME)
                .build();

        User professor = User.builder()
                .role(UserRole.PROFESSOR)
                .profile(profile)
                .build();

        profile.setUser(professor);
        userRepository.save(professor);
        this.professorId = professor.getId();

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
        this.moduleId = module.getId();
    }

    @Test
    public void testQuizCreation() {
        assertEquals(0, quizRepository.findAll().size());

        QuizCreationRequest request = new QuizCreationRequest("Quiz 1");

        Quiz quiz = sut.createQuiz(professorId, moduleId, request);

        assertNotNull(quiz.getId());
        assertEquals("Quiz 1", quiz.getTitle());
        assertEquals(1, quizRepository.findAll().size());
    }

    @Test
    public void testAddQuestion() {
        QuizCreationRequest quizRequest = new QuizCreationRequest("Quiz 1");
        Quiz quiz = sut.createQuiz(professorId, moduleId, quizRequest);

        assertEquals(0, quizQuestionRepository.findAll().size());
        assertEquals(0, quizAnswerOptionRepository.findAll().size());

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

        QuizQuestion question = sut.addQuestion(
                quiz.getId(),
                questionRequest,
                Set.of(opt1, opt2)
        );

        assertNotNull(question.getId());
        assertEquals("What is ORM?", question.getText());
        assertEquals(1, quizQuestionRepository.findAll().size());
        assertEquals(2, quizAnswerOptionRepository.findAll().size());
    }
}
