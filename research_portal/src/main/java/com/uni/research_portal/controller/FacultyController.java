package com.uni.research_portal.controller;

import com.uni.research_portal.dto.CountryArticleCountDto;
import com.uni.research_portal.dto.CreateFacultyDto;
import com.uni.research_portal.dto.InstitutionDto;
import com.uni.research_portal.dto.ResearchAreaDto;
import com.uni.research_portal.model.Faculty;
import com.uni.research_portal.service.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static com.uni.research_portal.util.Jwt.validateToken;

@RestController
@RequestMapping("faculty")
public class FacultyController {
    @Autowired
    FacultyService facultyService;

    @PostMapping("/")
    public Faculty createFaculty(@RequestBody CreateFacultyDto dto,
                                 @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token){
        if(validateToken(token.substring(7))){
            return facultyService.createFaculty(dto.getFacultyName(),token.substring(7));
        }
        else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFaculty(@PathVariable int id,
                                                @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        if (validateToken(token.substring(7))) {
            return facultyService.deleteFaculty(id, token.substring(7));
        } else {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }

    }
    @GetMapping("/")
    public List<Faculty> getFaculties(){
        return facultyService.getFaculties();
    }

    @GetMapping("/research-area/{id}")
    public List<ResearchAreaDto> getAreas(@PathVariable int id){
        return facultyService.getResearchAreas(id);
    }

    @GetMapping("/research-area/")
    public List<ResearchAreaDto> getAreas(){
        return facultyService.getResearchAreasUniversity();
    }

    @GetMapping("/institutions")
    public List<InstitutionDto> getInstitutions() {
        return facultyService.getInstitutions();
    }

    @GetMapping("/institutions-country")
    public List<CountryArticleCountDto> getInstitutionsCountry() {
        return facultyService.getTotalArticlesAndAverageCoordinatesByCountry();
    }



}
