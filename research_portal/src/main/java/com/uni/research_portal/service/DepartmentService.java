package com.uni.research_portal.service;

import com.uni.research_portal.dto.CreateDepartmentDto;
import com.uni.research_portal.model.Department;
import com.uni.research_portal.model.Faculty;
import com.uni.research_portal.repository.DepartmentRepository;
import com.uni.research_portal.repository.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DepartmentService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    FacultyRepository facultyRepository;

    public Department createDepartment(CreateDepartmentDto dto){
        Faculty fac = facultyRepository.findById(dto.getFacultyId()).get();
        Department newDepartment = new Department(fac, dto.getDepartmentName());
        departmentRepository.save(newDepartment);
        return newDepartment;
    }
}
