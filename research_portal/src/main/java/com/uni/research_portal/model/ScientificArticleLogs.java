package com.uni.research_portal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "scientific_article_logs")
public class ScientificArticleLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;

    @ManyToOne
    @JoinColumn(name = "article_id", referencedColumnName = "article_id")
    private ScientificArticle articleId;

    @Column(nullable = false)
    private String log;

    @Column
    private Date date;

    @Column
    private int oldValue;

    @Column
    private int newValue;

    public ScientificArticleLogs(ScientificArticle article, String log) {
        this.articleId = article;
        this.log = log;
        this.date = new Date();
    }

    public ScientificArticleLogs(ScientificArticle article, String log, int oldValue, int newValue) {
        this.articleId = article;
        this.log = log;
        this.date = new Date();
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}

