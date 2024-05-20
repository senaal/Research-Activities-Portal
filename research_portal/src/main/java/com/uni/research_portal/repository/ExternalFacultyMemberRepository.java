package com.uni.research_portal.repository;

import com.uni.research_portal.model.ExternalFacultyMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalFacultyMemberRepository extends JpaRepository<ExternalFacultyMember, Integer> {
    ExternalFacultyMember findByOpenAlexId(String id);

    ExternalFacultyMember findByExternalAuthorId(int id);
    ExternalFacultyMember findBySemanticId(int id);
}
