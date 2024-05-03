package com.uni.research_portal.dto;

import com.uni.research_portal.model.FacultyMember;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorInfoDto {
    private FacultyMember member;
    private int numberOfArticles;
    private int numberOfProjects;

}
