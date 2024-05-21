package com.uni.research_portal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "external_faculty_member")
public class ExternalFacultyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer externalAuthorId;

    @Column
    private String authorName;

    @ManyToOne
    @JoinColumn(name = "institution_id", referencedColumnName = "institution_id")
    private Institution institutionId;

    @Column
    private int semanticId;
    @Column(name="open_alex_id")
    private String openAlexId;
}
