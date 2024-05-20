package com.uni.research_portal.service;

import com.uni.research_portal.dto.CreateDepartmentDto;
import com.uni.research_portal.dto.ResearchAreaDto;
import com.uni.research_portal.model.*;
import com.uni.research_portal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    FacultyMemberRepository facultyMemberRepository;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    ResearchAreaRepository researchAreaRepository;

    @Autowired
    ResearchAreaAuthorRepository researchAreaAuthorRepository;



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

    public List<ResearchAreaDto> getResearchAreas(int id){
        Department department = departmentRepository.findByDepartmentId(id).get();
        List<FacultyMember> members = facultyMemberRepository.findByDepartmentIdDepartmentId(department.getDepartmentId());
        List<ResearchAreaAuthor> researchAreaAuthors = researchAreaAuthorRepository.findByAuthorIdIn(members);

        Map<Integer, Integer> researchAreaCountMap = researchAreaAuthors.stream()
                .collect(Collectors.groupingBy(
                        author -> author.getResearchAreaId().getResearchAreaId(),
                        Collectors.summingInt(ResearchAreaAuthor::getCount)
                ));

        List<ResearchAreaDto> response = new ArrayList<>();
        List<ResearchAreaDto> finalResponse = response;
        researchAreaCountMap.forEach((researchAreaId, count) -> {

            String fingerprintName = researchAreaAuthors.stream()
                    .filter(author -> author.getResearchAreaId().getResearchAreaId() == researchAreaId)
                    .findFirst()
                    .map(author -> author.getResearchAreaId().getFingerprintName())
                    .orElse(""); // Default to empty string if no matching author found

            ResearchAreaDto dto = new ResearchAreaDto(
                    fingerprintName,
                    researchAreaId,
                    count
            );
            response.add(dto);
        });

        response.sort(Comparator.comparingInt(ResearchAreaDto::getCount).reversed());
        if (response.size() > 10) {
            return response.subList(0, 10);
        }
        return response;
    }


}
