package com.uni.research_portal.repository;

import com.uni.research_portal.model.ArticleAuthor;
import com.uni.research_portal.model.ScientificArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ArticleAuthorRepository extends JpaRepository<ArticleAuthor, Integer> {
    Page<ArticleAuthor> findByAuthorIdAndIsFacultyMemberTrue(int id,  Pageable page);
    int countByAuthorId(int id);
    List<ArticleAuthor> findByScientificArticle(ScientificArticle article);

    Page<ArticleAuthor> findByIsFacultyMemberTrueAndAuthorIdIn(List<Integer> authorIds, Pageable pageable);

    Set<ArticleAuthor> findByIsFacultyMemberTrueAndAuthorIdIn(List<Integer> authorIds);



    Set<ArticleAuthor> findByIsFacultyMemberFalseAndScientificArticleIn(Set<ScientificArticle> scientificArticles);
}
