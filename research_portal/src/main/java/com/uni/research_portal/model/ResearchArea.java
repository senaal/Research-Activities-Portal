package com.uni.research_portal.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "research_area")
public class ResearchArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "research_area_id")
    private Long researchAreaId;

    @Column
    private String fingerprintName;

    @ManyToMany
    @JoinTable(name = "author_research_area",
            joinColumns = @JoinColumn(name = "research_area_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id"))
    private Set<FacultyMember> facultyMembers;
    @Column(name = "end_date")
    private java.sql.Timestamp endDate;
}
