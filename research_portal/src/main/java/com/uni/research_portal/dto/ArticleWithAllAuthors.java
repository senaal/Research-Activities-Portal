package com.uni.research_portal.dto;

import com.uni.research_portal.model.ScientificArticle;
import java.util.List;

public class ArticleWithAllAuthors {
    private ScientificArticle article;
    private List<String> authors;
    private Integer departmentId;

    // Constructor
    public ArticleWithAllAuthors(ScientificArticle article, List<String> authors, Integer departmentId) {
        this.article = article;
        this.authors = authors;
        this.departmentId = departmentId;
    }

    // Getters and setters
    public ScientificArticle getArticle() {
        return article;
    }

    public void setArticle(ScientificArticle article) {
        this.article = article;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }
}
