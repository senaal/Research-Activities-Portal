package com.uni.research_portal.repository;

import com.uni.research_portal.model.ScientificArticle;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScientificArticleRepository extends JpaRepository<ScientificArticle, Integer> {
    Optional<ScientificArticle> findByDoi(String doi);

    Optional<ScientificArticle> findByArticleIdAndIsRejectedFalse(int id);
}
