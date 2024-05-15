package com.uni.research_portal.repository;

import com.uni.research_portal.model.ArticleAuthor;
import com.uni.research_portal.model.ScientificArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleAuthorRepository extends JpaRepository<ArticleAuthor, Integer> {
    Page<ArticleAuthor> findByAuthorId(int id,  Pageable page);
    int countByAuthorId(int id);
    List<ArticleAuthor> findByScientificArticle(ScientificArticle article);

    Page<ArticleAuthor> findByIsFacultyMemberTrueAndAuthorIdIn(List<Integer> authorIds, Pageable pageable);

}
