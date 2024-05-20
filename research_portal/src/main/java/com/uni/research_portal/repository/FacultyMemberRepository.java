package com.uni.research_portal.repository;

import com.uni.research_portal.model.Department;
import com.uni.research_portal.model.FacultyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FacultyMemberRepository extends JpaRepository<FacultyMember, Integer> {
    List<FacultyMember> findByIsDeletedFalse();

    FacultyMember findByOpenAlexId(String id);

    Optional<FacultyMember> findByAuthorIdAndIsDeletedFalse(int id);

    List<FacultyMember> findByDepartmentIdAndIsDeletedFalse(Department department);

    List<FacultyMember> findByDepartmentIdDepartmentIdIn(List<Integer> departmentIds);

    List<FacultyMember> findByDepartmentIdIn(List<Department> departments);
    List<FacultyMember> findByDepartmentIdDepartmentId(Integer departmentId);
}
