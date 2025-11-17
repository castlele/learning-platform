package com.castlelecs.platform.service.course;

import com.castlelecs.platform.dto.ModuleCommand;
import com.castlelecs.platform.entity.Course;
import com.castlelecs.platform.repository.CourseRepository;
import com.castlelecs.platform.repository.ModuleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.castlelecs.platform.entity.Module;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;


    @Transactional
    public Module createModule(Long courseId, ModuleCommand command) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        Module module = Module.builder()
                .course(course)
                .title(command.getTitle())
                .description(command.getDescription())
                .orderIndex(command.getOrderIndex())
                .build();

        return moduleRepository.save(module);
    }

    @Transactional
    public Module updateModule(Long moduleId, ModuleCommand command) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Module not found: " + moduleId));

        if (command.getTitle() != null) {
            module.setTitle(command.getTitle());
        }
        if (command.getDescription() != null) {
            module.setDescription(command.getDescription());
        }
        if (command.getOrderIndex() != null) {
            module.setOrderIndex(command.getOrderIndex());
        }

        return moduleRepository.save(module);
    }

    @Transactional
    public void deleteModule(Long moduleId) {
        if (!moduleRepository.existsById(moduleId)) {
            throw new IllegalArgumentException("Module not found: " + moduleId);
        }
        moduleRepository.deleteById(moduleId);
    }

    @Transactional
    public Module getModule(Long moduleId) {
        return moduleRepository.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Module not found: " + moduleId));
    }

    @Transactional
    public Set<Module> getModulesForCourse(Long courseId) {
        return new HashSet<>(moduleRepository.findByCourseId(courseId));
    }

}
