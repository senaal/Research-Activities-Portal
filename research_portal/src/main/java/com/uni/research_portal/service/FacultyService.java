package com.uni.research_portal.service;

import com.uni.research_portal.model.Faculty;
import com.uni.research_portal.repository.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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


}
