package com.uni.research_portal.dto;

import com.uni.research_portal.model.ScientificArticle;

import java.util.List;

public class ArticleWithAuthorsDto {
    private ScientificArticle article;
    private List<String> authorNames;

    // Getters and setters
    public ScientificArticle getArticle() {
        return article;
    }

    public void setArticle(ScientificArticle article) {
        this.article = article;
    }

    public List<String> getAuthorNames() {
        return authorNames;
    }

    public void setAuthorNames(List<String> authorNames) {
        this.authorNames = authorNames;
    }
}
