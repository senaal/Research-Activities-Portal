package com.uni.research_portal.dto;

import com.uni.research_portal.model.Department;
import com.uni.research_portal.model.ScientificArticle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentArticlesDto {
        private ScientificArticle article;
        private String authorName;
        private Department department;

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
        public Department getDepartment(){return department;}
        public void setDepartment(Department department) {this.department = department;}

}

