package com.uni.research_portal.controller;


import com.uni.research_portal.model.FacultyMember;
import com.uni.research_portal.model.ScientificArticle;
import com.uni.research_portal.service.FacultyMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public FacultyMember getAuthorInfo(@PathVariable int id){
        return facultyMemberService.getAuthorInfo(id);
    }


}
