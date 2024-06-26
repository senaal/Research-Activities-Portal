package com.uni.research_portal.repository;

import com.uni.research_portal.model.ExternalFacultyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ExternalFacultyMemberRepository extends JpaRepository<ExternalFacultyMember, Integer> {
    ExternalFacultyMember findByOpenAlexId(String id);

    ExternalFacultyMember findByExternalAuthorId(int id);
    ExternalFacultyMember findBySemanticId(int id);

    Set<ExternalFacultyMember> findByExternalAuthorIdIn(Set<Integer> externalFacultyMemberIds);
}
