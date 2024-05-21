package com.uni.research_portal.controller;

import com.uni.research_portal.dto.CreateProjectDto;
import com.uni.research_portal.dto.ProjectsWithAuthorsDto;
import com.uni.research_portal.model.Project;
import com.uni.research_portal.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;


@RestController
@RequestMapping("/project")
public class ProjectController {
    @Autowired
    ProjectService projectService;

    @PostMapping("/")
    public Project addProject(@RequestBody CreateProjectDto createProjectDto){
        return projectService.addProject(createProjectDto);
    }

    @GetMapping("/")
    public Page<ProjectsWithAuthorsDto> getProjects(@RequestParam(defaultValue = "endDate") String sortBy,
                                                    @RequestParam(defaultValue = "DESC") String sortOrder,
                                                    @RequestParam(required = false) Integer page,
                                                    @RequestParam(required = false) Integer size
    ){

        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 10;
        return projectService.getProjects(sortBy,sortOrder,pageNum,pageSize);
    }

    @GetMapping("/faculty/{id}")
    public Page<ProjectsWithAuthorsDto> getFacultyProjects(@RequestParam(defaultValue = "endDate") String sortBy,
                                                    @RequestParam(defaultValue = "DESC") String sortOrder,
                                                    @RequestParam(required = false) Integer page,
                                                    @RequestParam(required = false) Integer size,
                                                    @PathVariable int id
    ){

        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 10;
        return projectService.getFacultyProjects(id,sortBy,sortOrder,pageNum,pageSize);
    }

    @GetMapping("/department/{id}")
    public Page<ProjectsWithAuthorsDto> getDepartmentProjects(@RequestParam(defaultValue = "endDate") String sortBy,
                                                           @RequestParam(defaultValue = "DESC") String sortOrder,
                                                           @RequestParam(required = false) Integer page,
                                                           @RequestParam(required = false) Integer size,
                                                           @PathVariable int id
    ){

        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 10;
        return projectService.getDepartmentProjects(id,sortBy,sortOrder,pageNum,pageSize);
    }
    @GetMapping("/author/{id}")
    public Page<ProjectsWithAuthorsDto> getAuthorProjects(@RequestParam(defaultValue = "endDate") String sortBy,
                                                              @RequestParam(defaultValue = "DESC") String sortOrder,
                                                              @RequestParam(required = false) Integer page,
                                                              @RequestParam(required = false) Integer size,
                                                              @PathVariable int id
    ){

        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 10;
        return projectService.getAuthorProjects(id,sortBy,sortOrder,pageNum,pageSize);
    }
}
