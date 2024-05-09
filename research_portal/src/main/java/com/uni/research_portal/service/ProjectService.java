package com.uni.research_portal.service;

import com.uni.research_portal.dto.AuthorDto;
import com.uni.research_portal.dto.CreateProjectDto;
import com.uni.research_portal.model.Project;
import com.uni.research_portal.model.ProjectAuthor;
import com.uni.research_portal.repository.ProjectAuthorRepository;
import com.uni.research_portal.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProjectService {
    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectAuthorRepository projectAuthorRepository;

    public Project addProject(CreateProjectDto createProjectDto){
        List<AuthorDto> authorIds = createProjectDto.getAuthorIds();
        Project newProject = new Project(createProjectDto.getProjectName(), createProjectDto.getStartDate(), createProjectDto.getEndDate());
        projectRepository.save(newProject);
        for (AuthorDto authorId: authorIds){
            ProjectAuthor newAuthor = new ProjectAuthor(newProject, authorId.getAuthorId(), authorId.getIsFacultyMember());
            projectAuthorRepository.save(newAuthor);

        }
        return newProject;
    }
}
