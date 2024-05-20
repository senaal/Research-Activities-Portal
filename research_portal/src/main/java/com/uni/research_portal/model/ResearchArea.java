package com.uni.research_portal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "research_area")
@Getter
@Setter
public class ResearchArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "research_area_id")
    private int researchAreaId;

    @Column
    private String fingerprintName;

    @Column
    private String openAlexId;

}
