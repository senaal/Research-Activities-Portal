package com.uni.research_portal.controller;


import com.uni.research_portal.dto.ArticleWithAllAuthors;
import com.uni.research_portal.dto.ArticleWithAuthorsDto;
import com.uni.research_portal.dto.DepartmentArticlesDto;
import com.uni.research_portal.model.Department;
import com.uni.research_portal.model.ScientificArticle;
import com.uni.research_portal.service.FacultyMemberService;
import com.uni.research_portal.service.ScientificArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("article")
public class ScientificArticleController {
    @Autowired
    ScientificArticleService scientificArticleService;

    @GetMapping("/sync")
    public ResponseEntity<String> syncScientificArticles(){
        return scientificArticleService.syncScientificArticles();
    }

    @GetMapping("/author/{id}")
    public Page<ArticleWithAuthorsDto> getAuthorArticles(@PathVariable int id,
                                                         @RequestParam(defaultValue = "publicationDate") String sortBy,
                                                         @RequestParam(defaultValue = "DESC") String sortOrder,
                                                         @RequestParam(required = false) Integer page,
                                                         @RequestParam(required = false) Integer size
        ){

        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 10;
        return scientificArticleService.getAuthorArticles(id,sortBy,sortOrder,pageNum,pageSize);
    }
    @GetMapping("/department/{departmentId}")
    public List<ArticleWithAllAuthors> getArticlesByDepartment(@PathVariable Integer departmentId) {
        return scientificArticleService.getArticlesByDepartment(departmentId);
    }

    @GetMapping("/scientific_articles")
    public Page<ArticleWithAuthorsDto> getAllArticles(   @RequestParam(defaultValue = "publicationDate") String sortBy,
                                                         @RequestParam(defaultValue = "DESC") String sortOrder,
                                                         @RequestParam(required = false) Integer page,
                                                         @RequestParam(required = false) Integer size
    ){

        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 10;
        return scientificArticleService.getScientificArticles(sortBy,sortOrder,pageNum,pageSize);
    }
}
