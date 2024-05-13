package com.uni.research_portal.controller;

import com.uni.research_portal.dto.CreateFacultyDto;
import com.uni.research_portal.model.Faculty;
import com.uni.research_portal.service.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("faculty")
public class FacultyController {
    @Autowired
    FacultyService facultyService;

    @PostMapping("/")
    public Faculty createFaculty(@RequestBody CreateFacultyDto dto){

        return facultyService.createFaculty(dto.getFacultyName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFaculty(@PathVariable int id){
        return facultyService.deleteFaculty(id);
    }

    @GetMapping("/")
    public List<Faculty> getFaculties(){
        return facultyService.getFaculties();
    }


}
