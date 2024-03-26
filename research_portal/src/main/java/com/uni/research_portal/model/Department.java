package com.uni.research_portal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Getter
@Setter
@Table(name = "department")
public class Department{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer departmentId;
    @ManyToOne
    @JoinColumn(name = "facultyId", referencedColumnName = "facultyId")
    private Faculty facultyId;

    private String departmentName;

}