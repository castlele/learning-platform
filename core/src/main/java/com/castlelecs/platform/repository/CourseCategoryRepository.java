package com.castlelecs.platform.repository;

import com.castlelecs.platform.entity.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Long> {
}
