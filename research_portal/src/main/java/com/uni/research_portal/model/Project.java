package com.uni.research_portal.model;

import jakarta.persistence.*;

@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private int projectId;

    @Column(nullable = false)
    private String projectName;

    @Column(name = "start_date")
    private java.sql.Timestamp startDate;
    @Column(name = "end_date")
    private java.sql.Timestamp endDate;
}
