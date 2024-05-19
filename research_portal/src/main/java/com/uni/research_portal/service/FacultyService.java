package com.uni.research_portal.service;

import com.uni.research_portal.model.Department;
import com.uni.research_portal.model.Faculty;
import com.uni.research_portal.repository.AdminRepository;
import com.uni.research_portal.repository.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.uni.research_portal.util.Jwt.extractSubject;

@Service
public class FacultyService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    FacultyRepository facultyRepository;
    @Autowired
    AdminRepository adminRepository;

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


}
