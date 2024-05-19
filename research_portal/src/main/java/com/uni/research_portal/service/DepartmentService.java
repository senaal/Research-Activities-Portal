package com.uni.research_portal.service;

import com.uni.research_portal.dto.CreateDepartmentDto;
import com.uni.research_portal.model.Department;
import com.uni.research_portal.model.Faculty;
import com.uni.research_portal.repository.AdminRepository;
import com.uni.research_portal.repository.DepartmentRepository;
import com.uni.research_portal.repository.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import static com.uni.research_portal.util.Jwt.extractSubject;

@Service
public class DepartmentService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    FacultyRepository facultyRepository;

    @Autowired
    AdminRepository adminRepository;

    public Department createDepartment(CreateDepartmentDto dto, String token){
        if (adminRepository.countByEmail(extractSubject(token))>0){
            Faculty fac = facultyRepository.findById(dto.getFacultyId()).get();
            Department newDepartment = new Department(fac, dto.getDepartmentName());
            departmentRepository.save(newDepartment);
            return newDepartment;
        }
        else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<String> deleteDepartment(int id, String token){
        if (adminRepository.countByEmail(extractSubject(token))>0){
            departmentRepository.deleteById(id);
            return new ResponseEntity<>("Department is deleted", HttpStatus.OK);
        }
        else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }

    public List<Department> getDepartments(){
        List<Department> departments = departmentRepository.findAll();
        return departments;
    }


}
