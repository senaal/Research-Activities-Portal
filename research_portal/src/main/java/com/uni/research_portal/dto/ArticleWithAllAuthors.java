package com.uni.research_portal.dto;

import com.uni.research_portal.model.Department;
import com.uni.research_portal.model.ScientificArticle;
import java.util.List;

public class ArticleWithAllAuthors {
    private ScientificArticle article;
    private List<String> authors;
    private Department department;

    // Constructor
    public ArticleWithAllAuthors(ScientificArticle article, List<String> authors, Department department) {
        this.article = article;
        this.authors = authors;
        this.department = department;
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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
