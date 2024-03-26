package com.uni.research_portal.repository;

import com.uni.research_portal.model.ScientificArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScientificArticleRepository extends JpaRepository<ScientificArticle, Integer> {
}
