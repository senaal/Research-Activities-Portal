package com.uni.research_portal.repository;

import com.uni.research_portal.model.ArticleAuthor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleAuthorRepository extends JpaRepository<ArticleAuthor, Integer> {
    List<ArticleAuthor> findByAuthorId(int id);
}
