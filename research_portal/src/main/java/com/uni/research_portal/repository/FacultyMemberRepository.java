package com.uni.research_portal.repository;

import com.uni.research_portal.model.FacultyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacultyMemberRepository extends JpaRepository<FacultyMember, Integer> {
    List<FacultyMember> findByIsDeletedFalse();

    FacultyMember findByOpenAlexId(String id);
}
