package com.uni.research_portal.repository;

import com.uni.research_portal.dto.DepartmentArticlesDto;
import com.uni.research_portal.dto.ExternalFacultyMemberDto;
import com.uni.research_portal.model.ArticleAuthor;
import com.uni.research_portal.model.ScientificArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScientificArticleRepository extends JpaRepository<ScientificArticle, Integer> {
    Optional<ScientificArticle> findByDoi(String doi);

    Optional<ScientificArticle> findByArticleIdAndIsRejectedFalse(int id);
    Page<ScientificArticle> findByArticleTitleContainingIgnoreCase(String title, Pageable page);
}

