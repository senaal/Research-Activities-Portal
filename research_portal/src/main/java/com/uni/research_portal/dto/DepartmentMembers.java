package com.uni.research_portal.dto;

import com.uni.research_portal.model.Department;
import com.uni.research_portal.model.FacultyMember;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class DepartmentMembers {
    Department department;
    List<FacultyMember> members;
}
