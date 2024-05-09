package com.uni.research_portal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uni.research_portal.dto.AuthorInfoDto;
import com.uni.research_portal.dto.CreateAuthorRequestDto;
import com.uni.research_portal.dto.DepartmentMembers;
import com.uni.research_portal.model.Department;
import com.uni.research_portal.model.FacultyMember;
import com.uni.research_portal.model.FacultyMemberLogs;
import com.uni.research_portal.repository.*;
import com.uni.research_portal.exception.ResourceNotFoundException;
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

    @Autowired
    FacultyMemberLogsRepository facultyMemberLogsRepository;
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

    public FacultyMember createFacultyMember(CreateAuthorRequestDto createAuthorRequestDto){
        Department department = departmentRepository.findByDepartmentId(createAuthorRequestDto.getDepartmentId()).get();
        FacultyMember newMember = new FacultyMember();
        newMember.setDepartmentId(department);
        newMember.setAuthorName(createAuthorRequestDto.getAuthorName());
        newMember.setOpenAlexId(createAuthorRequestDto.getOpenAlexId());
        newMember.setSemanticId(createAuthorRequestDto.getSemanticId());
        newMember.setEmail(createAuthorRequestDto.getEmail());
        newMember.setPhone(createAuthorRequestDto.getPhone());
        newMember.setAddress(createAuthorRequestDto.getAddress());
        newMember.setTitle(createAuthorRequestDto.getTitle());
        newMember.setPhoto(createAuthorRequestDto.getPhoto());
        facultyMemberRepository.save(newMember);
        FacultyMemberLogs facultyMemberLogs = new FacultyMemberLogs(newMember, "created");
        facultyMemberLogsRepository.save(facultyMemberLogs);
        return newMember;

    }

    public FacultyMember deleteFacultyMember(int id){
        FacultyMember deletedMember = facultyMemberRepository.findByAuthorIdAndIsDeletedFalse(id).get();
        deletedMember.setDeleted(true);
        facultyMemberRepository.save(deletedMember);
        FacultyMemberLogs facultyMemberLogs = new FacultyMemberLogs(deletedMember, "deleted");
        facultyMemberLogsRepository.save(facultyMemberLogs);
        return deletedMember;

    }

    public FacultyMember editFacultyMember(CreateAuthorRequestDto createAuthorRequestDto, int id){
        FacultyMember editedMember = facultyMemberRepository.findByAuthorIdAndIsDeletedFalse(id).get();
        editedMember.setEmail(createAuthorRequestDto.getEmail());
        editedMember.setPhone(createAuthorRequestDto.getPhone());
        editedMember.setAddress(createAuthorRequestDto.getAddress());
        editedMember.setTitle(createAuthorRequestDto.getTitle());
        editedMember.setPhoto(createAuthorRequestDto.getPhoto());
        facultyMemberRepository.save(editedMember);
        FacultyMemberLogs facultyMemberLogs = new FacultyMemberLogs(editedMember, "edited");
        facultyMemberLogsRepository.save(facultyMemberLogs);
        return editedMember;
    }

}

