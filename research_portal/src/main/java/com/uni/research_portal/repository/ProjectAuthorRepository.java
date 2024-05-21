package com.uni.research_portal.repository;

import com.uni.research_portal.model.Project;
import com.uni.research_portal.model.ProjectAuthor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectAuthorRepository extends JpaRepository<ProjectAuthor, Integer> {
    int countByAuthorId(int id);

    List<ProjectAuthor> findByProject(Project project);

    Page<ProjectAuthor> findByIsFacultyMemberTrueAndAuthorIdIn(List<Integer> facultyMemberIds, Pageable pageable);

    Page<ProjectAuthor> findByAuthorId(int authorId, Pageable pageable);
}
