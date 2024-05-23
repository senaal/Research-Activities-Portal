package com.uni.research_portal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionDto {
    private int institutionId;
    private String institutionName;
    private String country;
    private double x;
    private double y;
    private int articleCount;
}
