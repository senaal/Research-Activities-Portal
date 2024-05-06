package com.uni.research_portal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uni.research_portal.dto.ArticleWithAuthorsDto;
import com.uni.research_portal.dto.DepartmentArticlesDto;
import com.uni.research_portal.model.*;
import com.uni.research_portal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class ScientificArticleService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ScientificArticleRepository scientificArticleRepository;

    @Autowired
    ArticleAuthorRepository articleAuthorRepository;

    @Autowired
    FacultyMemberRepository facultyMemberRepository;
    @Autowired
    ExternalFacultyMemberRepository externalFacultyMemberRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    public void updateArticlesWithOpenAlex(String id) {
        try{
            String url = "https://api.openalex.org/works?filter=author.id:" + id;
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);
            String urlWithParam = uriBuilder.toUriString();
            ResponseEntity<String> response = restTemplate.exchange(urlWithParam, HttpMethod.GET, HttpEntity.EMPTY, String.class);
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                if (jsonNode != null) {
                    for (int i = 0; i< jsonNode.get("results").size();i++) {
                        JsonNode resultNode = jsonNode.get("results").get(i);
                        String doi = resultNode.get("doi").asText().substring("https://doi.org/".length());
                        Optional<ScientificArticle> article = scientificArticleRepository.findByDoi(doi);
                        // adding new article
                        if (article.isEmpty()) {
                            ScientificArticle newArticle = new ScientificArticle();
                            newArticle.setDoi(doi);
                            newArticle.setArticleTitle(resultNode.get("title").asText());
                            newArticle.setPublicationDate(Timestamp.valueOf(resultNode.get("publication_date").asText() + " 00:00:00"));
                            newArticle.setCitationCount(resultNode.get("cited_by_count").asInt());
                            if (resultNode.get("open_access").get("is_oa").asBoolean()) {
                                newArticle.setPaperPdf(resultNode.get("open_access").get("oa_url").asText());
                            } else {
                                newArticle.setOpenAccess(false);
                            }
                            scientificArticleRepository.save(newArticle);

                            //adding article author
                            for (JsonNode authorNode : resultNode.get("authorships")){
                                ArticleAuthor articleAuthor = new ArticleAuthor();
                                articleAuthor.setScientificArticle(newArticle);
                                String openAlexId = authorNode.get("author").get("id").asText().substring(authorNode.get("author").get("id").asText().lastIndexOf("/") + 1);
                                FacultyMember facultyMember = facultyMemberRepository.findByOpenAlexId(openAlexId);
                                if(facultyMember == null){
                                    ExternalFacultyMember externalFacultyMember = externalFacultyMemberRepository.findByOpenAlexId(openAlexId);
                                    articleAuthor.setIsFacultyMember(false);
                                    if(externalFacultyMember == null){
                                        ExternalFacultyMember newExtMember = new ExternalFacultyMember();
                                        newExtMember.setOpenAlexId(openAlexId);
                                        newExtMember.setAuthorName(authorNode.get("author").get("display_name").asText());
                                        newExtMember.setAffiliation(authorNode.get("institutions").get(0).get("display_name").asText());
                                        externalFacultyMemberRepository.save(newExtMember);
                                        articleAuthor.setIsFacultyMember(false);
                                        articleAuthor.setAuthorId(newExtMember.getExternalAuthorId());
                                    }else{
                                        articleAuthor.setAuthorId(externalFacultyMember.getExternalAuthorId());
                                    }
                                }else{
                                    articleAuthor.setIsFacultyMember(true);
                                    articleAuthor.setAuthorId(facultyMember.getAuthorId());
                                }

                                articleAuthorRepository.save(articleAuthor);

                            }
                        } else { // updating citation count for existing article
                            ScientificArticle article1 = article.get();
                            article1.setCitationCount(resultNode.get("cited_by_count").asInt());
                            scientificArticleRepository.save(article1);
                        }
                    }
                }
            }catch (Exception e) {throw new Exception(e);}
            new ResponseEntity<>("Article Synchronization Completed.", HttpStatus.OK);
        }catch(Exception e){
            new ResponseEntity<>("Check the Request.", HttpStatus.BAD_REQUEST);

        }
    }

    public ResponseEntity<String> syncScientificArticles() {
        try{
            List<FacultyMember> members = facultyMemberRepository.findByIsDeletedFalse();
            for (FacultyMember member : members) {
                updateArticlesWithOpenAlex(member.getOpenAlexId());
            }
            return new ResponseEntity<>("Synchronization Completed.", HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>("Check the Request.",HttpStatus.BAD_REQUEST);

        }
    }

    public Page<ArticleWithAuthorsDto> getAuthorArticles(int authorId, String sortBy, String sortOrder, int pageNumber, int pageSize) {
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<ArticleAuthor> articleAuthorsPage = articleAuthorRepository.findByAuthorId(authorId, pageable);

        return articleAuthorsPage.map(articleAuthor -> {
            List<ArticleAuthor> authorIds = articleAuthorRepository.findByScientificArticle(articleAuthor.getScientificArticle());
            List<String> authorNames = new ArrayList<>();

            for (ArticleAuthor auth: authorIds){
                String memberName;
                if(auth.getIsFacultyMember()){
                    memberName = facultyMemberRepository.findByAuthorIdAndIsDeletedFalse(auth.getAuthorId()).get().getAuthorName();
                } else {
                    memberName = externalFacultyMemberRepository.findByExternalAuthorId(auth.getAuthorId()).getAuthorName();
                }
                authorNames.add(memberName);
            }

            Optional<ScientificArticle> article = scientificArticleRepository.findByArticleIdAndIsRejectedFalse(articleAuthor.getScientificArticle().getArticleId());

            return article.map(articleObj -> {
                ArticleWithAuthorsDto articleDTO = new ArticleWithAuthorsDto();
                articleDTO.setArticle(articleObj);
                articleDTO.setAuthorNames(authorNames);
                return articleDTO;
            }).orElse(null); // Or throw an exception if article is not present
        });
    }

    public List<DepartmentArticlesDto> getArticlesByDepartment(int departmentId) {
        Department department = departmentRepository.findByDepartmentId(departmentId).get();

        return scientificArticleRepository.findByDepartmentId(departmentId);
    }
}

