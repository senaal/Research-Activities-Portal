package com.uni.research_portal.dto;

import lombok.Getter;
import lombok.Setter;
import com.uni.research_portal.model.Admin;

@Setter
@Getter
public class AdminResponseDto {
    public Admin admin;
    public String token;
}
