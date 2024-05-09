package com.uni.research_portal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "faculty_member")
public class FacultyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id")
    private Integer authorId;

    @ManyToOne
    @JoinColumn(name = "departmentId", referencedColumnName = "departmentId")
    private Department departmentId;

    @Column(nullable = false)
    private String authorName;

    @Column
    private String openAlexId;

    @Column
    private Integer semanticId;

    @Column
    private String email;

    @Column
    private String phone;

    @Column
    private String address;

    @Column
    private String title;

    @Column
    private Integer hIndex;

    @Column(name="i10_index")
    private Integer i10Index;

    @Column
    private Integer citedByCount;

    @Column
    private boolean isDeleted;

    @Column
    private String photo;
/*
    @ManyToMany(mappedBy = "facultyMembers")
    private Set<ResearchArea> researchAreas;*/
}