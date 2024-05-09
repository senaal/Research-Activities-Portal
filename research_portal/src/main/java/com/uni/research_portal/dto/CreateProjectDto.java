package com.uni.research_portal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProjectDto {
    private List<AuthorDto> authorIds;
    private String projectName;
    private Date startDate;
    private Date endDate;

}

