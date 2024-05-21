package com.uni.research_portal.service;

import com.uni.research_portal.dto.AuthorDto;
import com.uni.research_portal.dto.CreateProjectDto;
import com.uni.research_portal.dto.ProjectsWithAuthorsDto;
import com.uni.research_portal.model.*;
import com.uni.research_portal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ProjectService {
    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectAuthorRepository projectAuthorRepository;

    @Autowired
    FacultyMemberRepository facultyMemberRepository;

    @Autowired
    ExternalFacultyMemberRepository externalFacultyMemberRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    public Project addProject(CreateProjectDto createProjectDto){
        List<AuthorDto> authorIds = createProjectDto.getAuthorIds();
        Project newProject = new Project(createProjectDto.getProjectName(), createProjectDto.getStartDate(), createProjectDto.getEndDate(), createProjectDto.getLink());
        projectRepository.save(newProject);
        for (AuthorDto authorId: authorIds){
            ProjectAuthor newAuthor = new ProjectAuthor(newProject, authorId.getAuthorId(), authorId.getIsFacultyMember(),createProjectDto.getEndDate());
            projectAuthorRepository.save(newAuthor);

        }
        return newProject;
    }

    public Project getProjects(CreateProjectDto createProjectDto){
        List<AuthorDto> authorIds = createProjectDto.getAuthorIds();
        Project newProject = new Project(createProjectDto.getProjectName(), createProjectDto.getStartDate(), createProjectDto.getEndDate(), createProjectDto.getLink());
        projectRepository.save(newProject);
        for (AuthorDto authorId: authorIds){
            ProjectAuthor newAuthor = new ProjectAuthor(newProject, authorId.getAuthorId(), authorId.getIsFacultyMember());
            projectAuthorRepository.save(newAuthor);

        }
        return newProject;
    }

    public Page<ProjectsWithAuthorsDto> getProjects(String sortBy, String sortOrder, int pageNumber, int pageSize) {
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<Project> projectsP = projectRepository.findAll(pageable);

        return projectsP.map(projectMap -> {
            List<ProjectAuthor> authors = projectAuthorRepository.findByProject(projectMap);
            List<String> authorNames = new ArrayList<>();

            for (ProjectAuthor auth: authors){
                String memberName;
                if(auth.isFacultyMember()){
                    memberName = facultyMemberRepository.findByAuthorIdAndIsDeletedFalse(auth.getAuthorId()).get().getAuthorName();
                } else {
                    memberName = externalFacultyMemberRepository.findByExternalAuthorId(auth.getAuthorId()).getAuthorName();
                }
                authorNames.add(memberName);
            }

            Optional<Project> project = projectRepository.findByProjectId(projectMap.getProjectId());

            return project.map(articleObj -> {
                ProjectsWithAuthorsDto projectDto = new ProjectsWithAuthorsDto();
                projectDto.setProject(articleObj);
                projectDto.setAuthorNames(authorNames);
                return projectDto;
            }).orElse(null);
        });
    }

    public Page<ProjectsWithAuthorsDto> getFacultyProjects(int facultyId, String sortBy, String sortOrder, int pageNumber, int pageSize) {
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        List<Department> departments = departmentRepository.findDepartmentIdsByFacultyIdFacultyId(facultyId);
        List<Integer> departmentIds = new ArrayList<>();
        for (Department dept: departments){
            departmentIds.add(dept.getDepartmentId());
        }
        List<FacultyMember> facultyMembers = facultyMemberRepository.findByDepartmentIdDepartmentIdIn(departmentIds);
        List<Integer> facultyMemberIds = new ArrayList<>();
        for (FacultyMember fm: facultyMembers){
            facultyMemberIds.add(fm.getAuthorId());
        }
        Page<ProjectAuthor> projectAuthorsPage = projectAuthorRepository.findByIsFacultyMemberTrueAndAuthorIdIn(facultyMemberIds, pageable);

        List<Integer> projectIds = new ArrayList<>();
        return projectAuthorsPage.map(projectAuthor -> {
            List<ProjectAuthor> authorIds = projectAuthorRepository.findByProject(projectAuthor.getProject());
            List<String> authorNames = new ArrayList<>();

            for (ProjectAuthor auth: authorIds){
                String memberName;
                if(auth.isFacultyMember()){
                    memberName = facultyMemberRepository.findByAuthorIdAndIsDeletedFalse(auth.getAuthorId()).get().getAuthorName();
                } else {
                    memberName = externalFacultyMemberRepository.findByExternalAuthorId(auth.getAuthorId()).getAuthorName();
                }
                authorNames.add(memberName);
            }

            Optional<Project> project = projectRepository.findByProjectId(projectAuthor.getProject().getProjectId());

            return project.map(projectObj -> {
                if (projectIds.contains(projectObj.getProjectId())) {
                    return null;
                }
                else{
                    ProjectsWithAuthorsDto projectDto = new ProjectsWithAuthorsDto();
                    projectDto.setProject(projectObj);
                    projectDto.setAuthorNames(authorNames);
                    projectIds.add(projectObj.getProjectId());
                    return projectDto;
                }
            }).orElse(null);
        });
    }

    public Page<ProjectsWithAuthorsDto> getDepartmentProjects(int departmentId, String sortBy, String sortOrder, int pageNumber, int pageSize) {
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));

        List<FacultyMember> facultyMembers = facultyMemberRepository.findByDepartmentIdDepartmentId(departmentId);
        List<Integer> facultyMemberIds = new ArrayList<>();
        for (FacultyMember fm: facultyMembers){
            facultyMemberIds.add(fm.getAuthorId());
        }
        Page<ProjectAuthor> projectAuthorsPage = projectAuthorRepository.findByIsFacultyMemberTrueAndAuthorIdIn(facultyMemberIds, pageable);

        List<Integer> projectIds = new ArrayList<>();
        return projectAuthorsPage.map(projectAuthor -> {
            List<ProjectAuthor> authorIds = projectAuthorRepository.findByProject(projectAuthor.getProject());
            List<String> authorNames = new ArrayList<>();

            for (ProjectAuthor auth: authorIds){
                String memberName;
                if(auth.isFacultyMember()){
                    memberName = facultyMemberRepository.findByAuthorIdAndIsDeletedFalse(auth.getAuthorId()).get().getAuthorName();
                } else {
                    memberName = externalFacultyMemberRepository.findByExternalAuthorId(auth.getAuthorId()).getAuthorName();
                }
                authorNames.add(memberName);
            }

            Optional<Project> project = projectRepository.findByProjectId(projectAuthor.getProject().getProjectId());

            return project.map(projectObj -> {
                if (projectIds.contains(projectObj.getProjectId())) {
                    return null;
                }
                else{
                    ProjectsWithAuthorsDto projectDto = new ProjectsWithAuthorsDto();
                    projectDto.setProject(projectObj);
                    projectDto.setAuthorNames(authorNames);
                    projectIds.add(projectObj.getProjectId());
                    return projectDto;
                }
            }).orElse(null);
        });

    }

    public Page<ProjectsWithAuthorsDto> getAuthorProjects(int authorId, String sortBy, String sortOrder, int pageNumber, int pageSize) {
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<ProjectAuthor> projectAuthorPage = projectAuthorRepository.findByAuthorId(authorId, pageable);

        return projectAuthorPage.map(projectAuthor -> {
            List<ProjectAuthor> authorIds = projectAuthorRepository.findByProject(projectAuthor.getProject());
            List<String> authorNames = new ArrayList<>();

            for (ProjectAuthor auth: authorIds){
                String memberName;
                if(auth.isFacultyMember()){
                    memberName = facultyMemberRepository.findByAuthorIdAndIsDeletedFalse(auth.getAuthorId()).get().getAuthorName();
                } else {
                    memberName = externalFacultyMemberRepository.findByExternalAuthorId(auth.getAuthorId()).getAuthorName();
                }
                authorNames.add(memberName);
            }

            Optional<Project> project = projectRepository.findByProjectId(projectAuthor.getProject().getProjectId());

            return project.map(projectObj -> {
                ProjectsWithAuthorsDto projectDto = new ProjectsWithAuthorsDto();
                projectDto.setProject(projectObj);
                projectDto.setAuthorNames(authorNames);
                return projectDto;
            }).orElse(null);
        });
    }
}
