package com.uni.research_portal.controller;


import com.uni.research_portal.service.FacultyMemberService;
import com.uni.research_portal.service.ScientificArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("article")
public class ScientificArticleController {
    @Autowired
    ScientificArticleService scientificArticleService;

    @GetMapping("/sync")
    public ResponseEntity<String> syncScientificArticles(){
        return scientificArticleService.syncScientificArticles();
    }

}
