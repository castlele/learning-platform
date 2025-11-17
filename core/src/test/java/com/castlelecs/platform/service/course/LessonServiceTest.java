package com.castlelecs.platform.service.course;

import com.castlelecs.platform.dto.CourseCreationRequest;
import com.castlelecs.platform.dto.LessonCommand;
import com.castlelecs.platform.dto.ModuleCommand;
import com.castlelecs.platform.entity.*;
import com.castlelecs.platform.entity.Module;
import com.castlelecs.platform.repository.CourseRepository;
import com.castlelecs.platform.repository.LessonRepository;
import com.castlelecs.platform.repository.ModuleRepository;
import com.castlelecs.platform.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class LessonServiceTest {
    private static final String PROFESSOR_EMAIL = "someemail@somemail.do";
    private static final String PROFESSOR_FIRST_NAME = "Name";
    private static final String PROFESSOR_LAST_NAME = "NotName";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private LessonService sut;

    private Long moduleId;

    @BeforeEach
    public void setUp() {
        Profile profile = Profile.builder()
                .email(PROFESSOR_EMAIL)
                .firstName(PROFESSOR_FIRST_NAME)
                .lastName(PROFESSOR_LAST_NAME)
                .build();

        User user = User.builder()
                .role(UserRole.PROFESSOR)
                .profile(profile)
                .build();

        profile.setUser(user);
        userRepository.save(user);

        CourseCreationRequest courseRequest = new CourseCreationRequest(
                "ORM",
                "Nothing here",
                null,
                LocalDate.now(),
                LocalDate.now()
        );
        Course course = courseService.createCourse(user.getId(), courseRequest);

        ModuleCommand moduleRequest = new ModuleCommand(
                "Introduction",
                "Basics of ORM",
                1
        );
        Module module = moduleService.createModule(course.getId(), moduleRequest);
        this.moduleId = module.getId();
    }

    @Test
    public void testLessonCreation() {
        assertEquals(0, lessonRepository.findAll().size());

        LessonCommand request = new LessonCommand(
                "Lesson 1",
                "Lesson content"
        );

        Lesson lesson = sut.createLesson(moduleId, request);

        assertEquals("Lesson 1", lesson.getTitle());
        assertEquals(1, lessonRepository.findAll().size());
        assertEquals(lesson.getTitle(), lessonRepository.findAll().getFirst().getTitle());
    }

    @Test
    public void testLessonUpdate() {
        Module module = moduleRepository.findById(moduleId).orElseThrow();

        Lesson oldLesson = Lesson.builder()
                .module(module)
                .title("OLD")
                .content("OLD")
                .build();
        lessonRepository.save(oldLesson);

        LessonCommand updateRequest = new LessonCommand(
                "Updated lesson",
                "Updated content"
        );

        Lesson lesson = sut.updateLesson(oldLesson.getId(), updateRequest);

        assertEquals("Updated lesson", lesson.getTitle());
        assertEquals(1, lessonRepository.findAll().size());
        assertEquals(lesson.getTitle(), lessonRepository.findAll().getFirst().getTitle());
    }
}
