package com.uni.research_portal.controller;

import com.uni.research_portal.dto.AdminRequestDto;
import com.uni.research_portal.dto.AdminResponseDto;
import com.uni.research_portal.service.CitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("citation")
public class CitationController {

    @Autowired
    CitationService citationService;


    @GetMapping("/{id}")
    public Map<String, List<Integer>> citationResponse(@PathVariable int id){
        return citationService.getYearsAndCitationsByAuthorId(id);
    }

    @GetMapping("/department/{id}")
    public Map<String, List<Integer>> departmentCitationResponse(@PathVariable int id){
        return citationService.getDepartmentStatisticsByDepartmentId(id);
    }

    @GetMapping("/faculty/{id}")
    public Map<String, List<Integer>> facultyCitationResponse(@PathVariable int id){
        return citationService.getStatisticsByFacultyId(id);
    }
}
