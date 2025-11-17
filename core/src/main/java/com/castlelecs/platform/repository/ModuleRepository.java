package com.castlelecs.platform.repository;

import com.castlelecs.platform.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    Set<Module> findByCourseId(Long courseId);
}
