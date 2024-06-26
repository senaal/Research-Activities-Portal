package com.uni.research_portal.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "scientific_article")
public class ScientificArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Integer articleId;

    @Column(name = "paper_pdf")
    private String paperPdf;

    @Column(name = "affiliation")
    private String affiliation;

    @Column(name = "doi")
    private String doi;

    @Column(name = "article_title", length = 1024)
    private String articleTitle;

    @Column(name = "publication_date")
    private java.sql.Timestamp publicationDate;

    @Column(name = "citation_count")
    private Integer citationCount;

    @Column(name = "is_open_access")
    private boolean isOpenAccess;

    @Column(name = "is_rejected")
    private boolean isRejected;

    @Column(name = "source")
    private String source;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScientificArticle that = (ScientificArticle) o;
        return Objects.equals(articleId, that.articleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleId);
    }

}

