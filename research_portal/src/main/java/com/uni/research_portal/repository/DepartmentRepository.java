package com.uni.research_portal.repository;

import com.uni.research_portal.model.Department;
import com.uni.research_portal.model.ScientificArticle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    List<Department> findAll();
    Optional<Department> findByDepartmentId(int departmentId);
}
