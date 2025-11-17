package com.castlelecs.platform.service.course;

import com.castlelecs.platform.dto.LessonCommand;
import com.castlelecs.platform.entity.Lesson;
import com.castlelecs.platform.repository.LessonRepository;
import com.castlelecs.platform.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.castlelecs.platform.entity.Module;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    @Transactional
    public Lesson createLesson(Long moduleId, LessonCommand command) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Module not found: " + moduleId));

        Lesson lesson = Lesson.builder()
                .module(module)
                .title(command.getTitle())
                .content(command.getContent())
                .build();

        return lessonRepository.save(lesson);
    }

    @Transactional
    public Lesson updateLesson(Long lessonId, LessonCommand command) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found: " + lessonId));

        if (command.getTitle() != null) {
            lesson.setTitle(command.getTitle());
        }
        if (command.getContent() != null) {
            lesson.setContent(command.getContent());
        }

        return lessonRepository.save(lesson);
    }

    @Transactional
    public void deleteLesson(Long lessonId) {
        if (!lessonRepository.existsById(lessonId)) {
            throw new IllegalArgumentException("Lesson not found: " + lessonId);
        }
        lessonRepository.deleteById(lessonId);
    }

    @Transactional
    public Lesson getLesson(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found: " + lessonId));
    }

    @Transactional
    public Set<Lesson> getLessonsForModule(Long moduleId) {
        return new HashSet<>(lessonRepository.findByModuleId(moduleId));
    }
}
