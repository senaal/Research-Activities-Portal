package com.uni.research_portal.controller;

import com.uni.research_portal.dto.CreateProjectDto;
import com.uni.research_portal.model.Project;
import com.uni.research_portal.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/project")
public class ProjectController {
    @Autowired
    ProjectService projectService;

    @PostMapping("/")
    public Project addProject(@RequestBody CreateProjectDto createProjectDto){
        return projectService.addProject(createProjectDto);
    }
}
