package com.uni.research_portal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorDto {
    int authorId;
    boolean isFacultyMember;

    public boolean getIsFacultyMember() {
        return isFacultyMember;
    }

    public int getAuthorId() {
        return authorId;
    }
}