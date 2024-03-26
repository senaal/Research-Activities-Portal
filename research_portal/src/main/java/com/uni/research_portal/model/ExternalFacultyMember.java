package com.uni.research_portal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "external_faculty_member")
public class ExternalFacultyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long externalAuthorId;

    @Column
    private String authorName;

    @Column
    private String affiliation;
}
