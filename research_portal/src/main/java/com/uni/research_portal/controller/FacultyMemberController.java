package com.uni.research_portal.controller;


import com.uni.research_portal.dto.AuthorInfoDto;
import com.uni.research_portal.dto.CreateAuthorRequestDto;
import com.uni.research_portal.dto.DepartmentMembers;
import com.uni.research_portal.model.FacultyMember;
import com.uni.research_portal.model.ScientificArticle;
import com.uni.research_portal.service.FacultyMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("facultymember")
public class FacultyMemberController {
    @Autowired
    FacultyMemberService facultyMemberService;

    @GetMapping("/sync")
    public ResponseEntity<String> syncFacultyMembers(){
        return facultyMemberService.syncFacultyMembers();
    }

    @GetMapping("/{id}")
    public AuthorInfoDto getAuthorInfo(@PathVariable int id){
        return facultyMemberService.getAuthorInfo(id);
    }

    @GetMapping("/")
    public List<DepartmentMembers> getAProfiles(){
        return facultyMemberService.getMembers();
    }

    @GetMapping("/all")
    public List<FacultyMember> getMembers(){
        return facultyMemberService.getAllMembers();
    }

    @PostMapping("/")
    public FacultyMember createFacultyMember(@RequestBody CreateAuthorRequestDto createAuthorRequestDto){
        return facultyMemberService.createFacultyMember(createAuthorRequestDto);
    }

    @DeleteMapping("/{id}")
    public FacultyMember deleteFacultyMember(@PathVariable int id){
        return facultyMemberService.deleteFacultyMember(id);
    }

    @PutMapping("/{id}")
    public FacultyMember putMapping(@RequestBody CreateAuthorRequestDto createAuthorRequestDto, @PathVariable int id){
        return facultyMemberService.editFacultyMember(createAuthorRequestDto,id);
    }


}
