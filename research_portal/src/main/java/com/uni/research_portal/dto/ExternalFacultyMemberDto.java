package com.uni.research_portal.dto;

import com.uni.research_portal.model.ScientificArticle;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class ExternalFacultyMemberDto {
    private ScientificArticle article;
    private String authorName;

    public ScientificArticle getArticle() {
        return article;
    }
    public void setArticle(ScientificArticle article) {
        this.article = article;
    }
    public String getAuthorName() {
        return authorName;
    }
    public void setAuthorNames( String authorName) {
        this.authorName = authorName;
    }
}
