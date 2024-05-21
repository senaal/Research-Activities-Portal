package com.uni.research_portal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    @Column
    private boolean isFacultyMember;

    @Column(name = "end_date")
    private Date endDate;

    public ProjectAuthor(Project project, Integer authorId, Boolean isFacultyMember) {
        this.project = project;
        this.authorId = authorId;
        this.isFacultyMember = isFacultyMember;
    }
    public ProjectAuthor(Project project, Integer authorId, Boolean isFacultyMember,Date endDate) {
        this.project = project;
        this.authorId = authorId;
        this.isFacultyMember = isFacultyMember;
        this.endDate = endDate;
    }
}

