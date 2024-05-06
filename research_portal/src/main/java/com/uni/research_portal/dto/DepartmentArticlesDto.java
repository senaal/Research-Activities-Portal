package com.uni.research_portal.dto;

import com.uni.research_portal.model.ScientificArticle;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class DepartmentArticlesDto {
        private ScientificArticle article;
        private String authorName;
        private Integer departmentId;


    // Getters and setters
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
        public Integer getDepartmentId(){return departmentId;}
        public void setDepartmentId(int departmentId) {this.departmentId = departmentId;}

}

