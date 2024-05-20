package com.uni.research_portal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "research_area_author")
@Getter
@Setter
public class ResearchAreaAuthor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "research_area_author_id")
    private int researchAreaAuthorId;

    @Column
    private int count;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "author_id")
    private FacultyMember authorId;

    @ManyToOne
    @JoinColumn(name = "research_area_id", referencedColumnName = "research_area_id")
    private ResearchArea researchAreaId;


}
