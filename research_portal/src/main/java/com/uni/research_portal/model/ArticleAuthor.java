package com.uni.research_portal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "article_author")
public class ArticleAuthor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_author_id")
    private Integer articleAuthorId;

    @ManyToOne
    @JoinColumn(name = "article_id", referencedColumnName = "article_id")
    private ScientificArticle scientificArticle;

    @Column
    private Integer authorId;

    @Column
    private Boolean isFacultyMember;

    @Column
    private int citationCount;

    @Column
    private java.sql.Timestamp publicationDate;

}

