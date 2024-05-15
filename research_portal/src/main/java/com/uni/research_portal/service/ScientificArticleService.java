package com.uni.research_portal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uni.research_portal.dto.*;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Autowired
    FacultyRepository facultyRepository;

    @Autowired
    ScientificArticleLogsRepostory scientificArticleLogsRepostory;
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
                            scientificArticleLogsRepostory.save(new ScientificArticleLogs(newArticle, "created"));

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
                            int citCount = resultNode.get("cited_by_count").asInt();
                            if (article1.getCitationCount() !=  citCount){
                                int coutn = article1.getCitationCount();
                                article1.setCitationCount(resultNode.get("cited_by_count").asInt());
                                scientificArticleRepository.save(article1);
                                ScientificArticleLogs log = new ScientificArticleLogs(article1, "updated citation count", coutn, resultNode.get("cited_by_count").asInt());
                                scientificArticleLogsRepostory.save(log);
                            }

                        }
                    }
                }
            }catch (Exception e) {throw new Exception(e);}
            new ResponseEntity<>("Article Synchronization Completed.", HttpStatus.OK);
        }catch(Exception e){
            new ResponseEntity<>("Check the Request.", HttpStatus.BAD_REQUEST);

        }
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "GMT+3")
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
            }).orElse(null);
        });
    }

    public Page<ArticleWithAuthorsDto> getArticlesByDepartment(int departmentId, String sortBy, String sortOrder, int pageNumber, int pageSize) {
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));

        List<FacultyMember> facultyMembers = facultyMemberRepository.findByDepartmentIdDepartmentId(departmentId);
        List<Integer> facultyMemberIds = new ArrayList<>();
        for (FacultyMember fm: facultyMembers){
            facultyMemberIds.add(fm.getAuthorId());
        }
        Page<ArticleAuthor> articleAuthorsPage = articleAuthorRepository.findByIsFacultyMemberTrueAndAuthorIdIn(facultyMemberIds, pageable);

        List<Integer> articleIds = new ArrayList<>();
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
                if (articleIds.contains(articleObj.getArticleId())) {
                    return null;
                }
                else{
                    ArticleWithAuthorsDto articleDTO = new ArticleWithAuthorsDto();
                    articleDTO.setArticle(articleObj);
                    articleDTO.setAuthorNames(authorNames);
                    articleIds.add(articleObj.getArticleId());
                    return articleDTO;
                }
            }).orElse(null);
        });

    }

    public Page<ArticleWithAuthorsDto> getScientificArticles(String sortBy, String sortOrder, int pageNumber, int pageSize) {
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<ScientificArticle> articleAuthorsPage = scientificArticleRepository.findAll(pageable);

        return articleAuthorsPage.map(articleDto -> {
            List<ArticleAuthor> authorIds = articleAuthorRepository.findByScientificArticle(articleDto);
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

            Optional<ScientificArticle> article = scientificArticleRepository.findByArticleIdAndIsRejectedFalse(articleDto.getArticleId());

            return article.map(articleObj -> {
                ArticleWithAuthorsDto articleDTO = new ArticleWithAuthorsDto();
                articleDTO.setArticle(articleObj);
                articleDTO.setAuthorNames(authorNames);
                return articleDTO;
            }).orElse(null);
        });
    }

    public Page<ArticleWithAuthorsDto> getArticlesByFaculty(int facultyId, String sortBy, String sortOrder, int pageNumber, int pageSize) {
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        List<Department> departments = departmentRepository.findDepartmentIdsByFacultyIdFacultyId(facultyId);
        List<Integer> departmentIds = new ArrayList<>();
        for (Department dept: departments){
            departmentIds.add(dept.getDepartmentId());
        }
        List<FacultyMember> facultyMembers = facultyMemberRepository.findByDepartmentIdDepartmentIdIn(departmentIds);
        List<Integer> facultyMemberIds = new ArrayList<>();
        for (FacultyMember fm: facultyMembers){
            facultyMemberIds.add(fm.getAuthorId());
        }
        Page<ArticleAuthor> articleAuthorsPage = articleAuthorRepository.findByIsFacultyMemberTrueAndAuthorIdIn(facultyMemberIds, pageable);

        List<Integer> articleIds = new ArrayList<>();
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
                if (articleIds.contains(articleObj.getArticleId())) {
                    return null;
                }
                else{
                    ArticleWithAuthorsDto articleDTO = new ArticleWithAuthorsDto();
                    articleDTO.setArticle(articleObj);
                    articleDTO.setAuthorNames(authorNames);
                    articleIds.add(articleObj.getArticleId());
                    return articleDTO;
                }
            }).orElse(null);
        });
    }
}

