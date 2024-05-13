package com.uni.research_portal.service;

import com.uni.research_portal.model.Department;
import com.uni.research_portal.model.Faculty;
import com.uni.research_portal.repository.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FacultyService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    FacultyRepository facultyRepository;

    public Faculty createFaculty(String name){
        Faculty newFaculty = new Faculty(name);
        facultyRepository.save(newFaculty);
        return newFaculty;
    }

    public ResponseEntity<String> deleteFaculty(int id){
        facultyRepository.deleteById(id);
        return new ResponseEntity<>("Faculty is deleted", HttpStatus.OK);
    }

    public List<Faculty> getFaculties(){
        List<Faculty> faculties = facultyRepository.findAll();
        return faculties;
    }


}
