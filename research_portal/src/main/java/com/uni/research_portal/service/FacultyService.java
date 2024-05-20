package com.uni.research_portal.service;

import com.uni.research_portal.dto.ResearchAreaDto;
import com.uni.research_portal.model.Department;
import com.uni.research_portal.model.Faculty;
import com.uni.research_portal.model.FacultyMember;
import com.uni.research_portal.model.ResearchAreaAuthor;
import com.uni.research_portal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.uni.research_portal.util.Jwt.extractSubject;

@Service
public class FacultyService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    FacultyRepository facultyRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    FacultyMemberRepository facultyMemberRepository;
    @Autowired
    AdminRepository adminRepository;
    @Autowired
    ResearchAreaAuthorRepository researchAreaAuthorRepository;

    public Faculty createFaculty(String name, String token){
        if (adminRepository.countByEmail(extractSubject(token))>0){
            Faculty newFaculty = new Faculty(name);
            facultyRepository.save(newFaculty);
            return newFaculty;
        }
        else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }


    public ResponseEntity<String> deleteFaculty(int id, String token){
        if (adminRepository.countByEmail(extractSubject(token))>0){
            facultyRepository.deleteById(id);
            return new ResponseEntity<>("Faculty is deleted", HttpStatus.OK);
        }
        else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }

    public List<Faculty> getFaculties(){
        List<Faculty> faculties = facultyRepository.findAll();
        return faculties;
    }

    public List<ResearchAreaDto> getResearchAreas(int id){
        Faculty faculty = facultyRepository.findById(id).get();
        List<Department> departments = departmentRepository.findByFacultyId(faculty);
        List<FacultyMember> members = facultyMemberRepository.findByDepartmentIdIn(departments);
        List<ResearchAreaAuthor> researchAreaAuthors = researchAreaAuthorRepository.findByAuthorIdIn(members);
        Map<Integer, Integer> researchAreaCountMap = researchAreaAuthors.stream()
                .collect(Collectors.groupingBy(
                        author -> author.getResearchAreaId().getResearchAreaId(),
                        Collectors.summingInt(ResearchAreaAuthor::getCount)
                ));

        List<ResearchAreaDto> response = new ArrayList<>();
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

    public List<ResearchAreaDto> getResearchAreasUniversity(){
        List<FacultyMember> members = facultyMemberRepository.findByIsDeletedFalse();
        List<ResearchAreaAuthor> researchAreaAuthors = researchAreaAuthorRepository.findByAuthorIdIn(members);

        Map<Integer, Integer> researchAreaCountMap = researchAreaAuthors.stream()
                .collect(Collectors.groupingBy(
                        author -> author.getResearchAreaId().getResearchAreaId(),
                        Collectors.summingInt(ResearchAreaAuthor::getCount)
                ));

        List<ResearchAreaDto> response = new ArrayList<>();
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
