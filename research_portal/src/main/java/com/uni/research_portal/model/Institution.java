package com.uni.research_portal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "institution")
public class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "institution_id")
    private Integer institutionId;

    private String name;

    private double xCoordinate;

    private double yCoordinate;

    private String ror;

    public Institution(String name, double xCoordinate, double yCoordinate, String ror) {
        this.name = name;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.ror = ror;
    }
}
