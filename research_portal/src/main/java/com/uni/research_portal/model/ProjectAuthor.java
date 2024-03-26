package com.uni.research_portal.model;

import jakarta.persistence.*;

@Entity
@Table(name = "project_author")
public class ProjectAuthor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_author_id")
    private Long projectAuthorId;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "project_id")
    private Project project;

    @Column
    private Integer authorId;

}

