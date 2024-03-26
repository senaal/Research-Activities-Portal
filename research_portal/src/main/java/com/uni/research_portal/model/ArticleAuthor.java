package com.uni.research_portal.model;

import jakarta.persistence.*;

@Entity
@Table(name = "article_author")
public class ArticleAuthor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_author_id")
    private Long articleAuthorId;

    @ManyToOne
    @JoinColumn(name = "article_id", referencedColumnName = "article_id")
    private ScientificArticle scientificArticle;

    @Column
    private Integer authorId;

}

