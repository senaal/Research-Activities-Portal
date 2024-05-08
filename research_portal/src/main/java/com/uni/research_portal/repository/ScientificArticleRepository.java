package com.uni.research_portal.repository;

import com.uni.research_portal.dto.DepartmentArticlesDto;
import com.uni.research_portal.dto.ExternalFacultyMemberDto;
import com.uni.research_portal.model.Department;
import com.uni.research_portal.model.FacultyMember;
import com.uni.research_portal.model.ScientificArticle;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScientificArticleRepository extends JpaRepository<ScientificArticle, Integer> {
    Optional<ScientificArticle> findByDoi(String doi);

    Optional<ScientificArticle> findByArticleIdAndIsRejectedFalse(int id);

    @Query("SELECT NEW com.uni.research_portal.dto.DepartmentArticlesDto(sa, CAST(string_agg(fm.authorName, ', ') AS text), d) " +
            "FROM ScientificArticle sa " +
            "JOIN ArticleAuthor aa ON sa.articleId = aa.scientificArticle.articleId " +
            "JOIN FacultyMember fm ON aa.authorId = fm.authorId " +
            "JOIN Department d ON fm.departmentId.departmentId = d.departmentId " +
            "WHERE d.departmentId = :departmentId " +
            "GROUP BY sa.articleId, d.departmentId")
    List<DepartmentArticlesDto> findByDepartmentId(@Param("departmentId") int departmentId);

    @Query("SELECT new com.uni.research_portal.dto.ExternalFacultyMemberDto(sa, CAST(string_agg(efm.authorName, ', ') AS text)) " +
            "FROM ScientificArticle sa " +
            "JOIN ArticleAuthor aa ON sa.articleId = aa.scientificArticle.articleId " +
            "JOIN ExternalFacultyMember efm ON aa.authorId = efm.externalAuthorId " +
            "GROUP BY sa.articleId")
    List<ExternalFacultyMemberDto> findArticlesWithAuthors();
}

