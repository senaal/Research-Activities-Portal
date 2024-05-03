package com.uni.research_portal.repository;

import com.uni.research_portal.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    Project findByProjectId(int id);
}
