package com.uni.research_portal.controller;


import com.uni.research_portal.dto.AuthorInfoDto;
import com.uni.research_portal.dto.CreateAuthorRequestDto;
import com.uni.research_portal.dto.DepartmentMembers;
import com.uni.research_portal.dto.ResearchAreaDto;
import com.uni.research_portal.model.FacultyMember;
import com.uni.research_portal.model.ScientificArticle;
import com.uni.research_portal.service.FacultyMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static com.uni.research_portal.util.Jwt.validateToken;

@RestController
@RequestMapping("facultymember")
public class FacultyMemberController {
    @Autowired
    FacultyMemberService facultyMemberService;

    @GetMapping("/sync")
    public ResponseEntity<String> syncFacultyMembers( @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        if (validateToken(token.substring(7))) {
            return facultyMemberService.syncFacultyMembers();
        }
        else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
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
    public FacultyMember createFacultyMember(@RequestBody CreateAuthorRequestDto createAuthorRequestDto,
                                             @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        if (validateToken(token.substring(7))) {
            return facultyMemberService.createFacultyMember(createAuthorRequestDto, token.substring(7));
        }
        else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/{id}")
    public FacultyMember deleteFacultyMember(@PathVariable int id,
                                             @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        if (validateToken(token.substring(7))) {
            return facultyMemberService.deleteFacultyMember(id, token.substring(7));
        }
        else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/{id}")
    public FacultyMember putMapping(@RequestBody CreateAuthorRequestDto createAuthorRequestDto, @PathVariable int id,
                                    @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        if (validateToken(token.substring(7))) {
            return facultyMemberService.editFacultyMember(createAuthorRequestDto,id, token);
        }else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/research-area")
    public ResponseEntity<String> addAreas(){
        return facultyMemberService.addResearchAreas();
    }

    @GetMapping("/research-area/{id}")
    public List<ResearchAreaDto> getAreas(@PathVariable int id){
        return facultyMemberService.getResearchAreas(id);
    }

}
