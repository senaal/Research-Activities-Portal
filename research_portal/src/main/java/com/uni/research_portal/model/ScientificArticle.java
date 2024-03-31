package com.uni.research_portal.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "scientific_article")
public class ScientificArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long articleId;

    @Column(name = "paper_pdf")
    private String paperPdf;

    @Column(name = "affiliation", nullable = false)
    private String affiliation;

    @Column(name = "doi", nullable = false)
    private String doi;

    @Column(name = "article_title", nullable = false)
    private String articleTitle;

    @Column(name = "publication_date", nullable = false)
    private java.sql.Timestamp publicationDate;

    @Column(name = "citation_count", nullable = false)
    private Integer citationCount;

    @Column(name = "is_open_access", nullable = false)
    private boolean isOpenAccess;

    @Column(name = "is_rejected", nullable = false)
    private boolean isRejected;

}

