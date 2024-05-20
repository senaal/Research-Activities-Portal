package com.uni.research_portal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "citations")
public class Citations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "citation_id")
    private Integer citationId;

    @Column
    private Integer authorId;

    @Column
    private int year;

    @Column
    private int workCount;

    @Column
    private int citedByCount;
}
