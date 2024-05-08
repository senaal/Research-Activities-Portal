package com.uni.research_portal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uni.research_portal.dto.AuthorInfoDto;
import com.uni.research_portal.dto.DepartmentMembers;
import com.uni.research_portal.model.Department;
import com.uni.research_portal.model.FacultyMember;
import com.uni.research_portal.repository.ArticleAuthorRepository;
import com.uni.research_portal.repository.DepartmentRepository;
import com.uni.research_portal.repository.FacultyMemberRepository;
import com.uni.research_portal.exception.ResourceNotFoundException;
import com.uni.research_portal.repository.ProjectAuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FacultyMemberService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    FacultyMemberRepository facultyMemberRepository;

    @Autowired
    ArticleAuthorRepository articleAuthorRepository;

    @Autowired
    ProjectAuthorRepository projectAuthorRepository;

    @Autowired
    DepartmentRepository departmentRepository;
    public void updateMembers(String id) {
        try{
            String url = "https://api.openalex.org/authors/" + id;
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url)
                    .queryParam("select", "display_name,cited_by_count,summary_stats");
            String urlWithParam = uriBuilder.toUriString();
            ResponseEntity<String> response = restTemplate.exchange(urlWithParam, HttpMethod.GET, HttpEntity.EMPTY, String.class);
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            FacultyMember facultyMember = facultyMemberRepository.findByOpenAlexId(id);
            try {
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                if (jsonNode != null) {
                    facultyMember.setCitedByCount(jsonNode.get("cited_by_count").asInt());
                    facultyMember.setHIndex(jsonNode.get("summary_stats").get("h_index").asInt());
                    facultyMember.setI10Index(jsonNode.get("summary_stats").get("i10_index").asInt());
                    facultyMemberRepository.save(facultyMember);
                }
            } catch (Exception ignored) {}
            new ResponseEntity<>("Synchronization Completed.", HttpStatus.OK);
        }catch(Exception e){
            new ResponseEntity<>("Check the Request.", HttpStatus.BAD_REQUEST);

        }
    }

    public ResponseEntity<String> syncFacultyMembers() {
        try{
            List<FacultyMember> members = facultyMemberRepository.findByIsDeletedFalse();
            for (FacultyMember member : members) {
                updateMembers(member.getOpenAlexId());
            }
            return new ResponseEntity<>("Synchronization Completed.", HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>("Check the Request.",HttpStatus.BAD_REQUEST);

        }
    }

    public AuthorInfoDto getAuthorInfo(int authorId) {
        Optional<FacultyMember> member = facultyMemberRepository.findByAuthorIdAndIsDeletedFalse(authorId);
        if(member.isPresent()){
            AuthorInfoDto response = new AuthorInfoDto();
            response.setMember(member.get());
            int articleCount = articleAuthorRepository.countByAuthorId(member.get().getAuthorId());
            int projectCount = projectAuthorRepository.countByAuthorId(member.get().getAuthorId());
            response.setNumberOfArticles(articleCount);
            response.setNumberOfProjects(projectCount);
            return response;

        }
        else{
            throw new ResourceNotFoundException(String.format("Faculty member with id %s is not found", authorId));
        }
    }

    public List<DepartmentMembers> getMembers() {
        ArrayList<DepartmentMembers> response = new ArrayList<>();
        List<Department> departments = departmentRepository.findAll();
        for(Department department: departments) {
            List<FacultyMember> members = facultyMemberRepository.findByDepartmentIdAndIsDeletedFalse(department);
            DepartmentMembers deptMem = new DepartmentMembers();
            deptMem.setDepartment(department);
            deptMem.setMembers(members);
            response.add(deptMem);
        }
        return response;
    }

}

