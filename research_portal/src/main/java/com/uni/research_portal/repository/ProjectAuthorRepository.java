package com.uni.research_portal.repository;

import com.uni.research_portal.model.ProjectAuthor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectAuthorRepository extends JpaRepository<ProjectAuthor, Integer> {
    int countByAuthorId(int id);
}
