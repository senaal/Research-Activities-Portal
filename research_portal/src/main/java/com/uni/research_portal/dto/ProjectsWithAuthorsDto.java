package com.uni.research_portal.dto;

import com.uni.research_portal.model.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectsWithAuthorsDto {
    private Project project;
    private List<String> authorNames;

}
