package com.uni.research_portal.repository;

import com.uni.research_portal.model.FacultyMember;
import com.uni.research_portal.model.ResearchAreaAuthor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResearchAreaAuthorRepository extends JpaRepository<ResearchAreaAuthor, Integer> {
    List<ResearchAreaAuthor> findByAuthorId(FacultyMember member);
    List<ResearchAreaAuthor> findByAuthorIdIn(List<FacultyMember> member);

}
