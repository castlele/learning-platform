package com.castlelecs.platform.service.course;

import com.castlelecs.platform.entity.Module;
import com.castlelecs.platform.dto.CourseCreationRequest;
import com.castlelecs.platform.dto.ModuleCommand;
import com.castlelecs.platform.entity.Course;
import com.castlelecs.platform.entity.Profile;
import com.castlelecs.platform.entity.User;
import com.castlelecs.platform.entity.UserRole;
import com.castlelecs.platform.repository.CourseRepository;
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
public class ModuleServiceTest {
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
    private CourseService courseService;

    @Autowired
    private ModuleService sut;

    private Long courseId;

    @BeforeEach
    public void setUp() {
        // create professor
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
        this.courseId = course.getId();
    }

    @Test
    public void testModuleCreation() {
        assertEquals(0, moduleRepository.findAll().size());

        ModuleCommand request = new ModuleCommand(
                "Introduction",
                "Basics of ORM",
                1
        );

        Module module = sut.createModule(courseId, request);

        assertEquals("Introduction", module.getTitle());
        assertEquals(1, moduleRepository.findAll().size());
        assertEquals(module.getTitle(), moduleRepository.findAll().getFirst().getTitle());
    }

    @Test
    public void testModuleUpdate() {
        Course course = courseRepository.findById(courseId).orElseThrow();

        Module oldModule = Module.builder()
                .course(course)
                .title("OLD")
                .description("OLD")
                .orderIndex(1)
                .build();
        moduleRepository.save(oldModule);

        ModuleCommand updateRequest = new ModuleCommand(
                "Updated title",
                "Updated description",
                2
        );

        Module module = sut.updateModule(oldModule.getId(), updateRequest);

        assertEquals("Updated title", module.getTitle());
        assertEquals(1, moduleRepository.findAll().size());
        assertEquals(module.getTitle(), moduleRepository.findAll().getFirst().getTitle());
    }
}
