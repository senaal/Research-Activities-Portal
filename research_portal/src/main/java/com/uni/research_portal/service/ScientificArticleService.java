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
import java.util.List;
import java.util.Objects;
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
    @Autowired
    FacultyRepository facultyRepository;

    @Autowired
    ScientificArticleLogsRepostory scientificArticleLogsRepostory;
    public void updateArticlesWithOpenAlex(String id) {
        try{
            String url = "https://api.openalex.org/works?filter=author.id:" + id;
            UriComponentsBuilder uriBuilderMeta = UriComponentsBuilder.fromUriString(url);
            String urlWithParamMeta = uriBuilderMeta.toUriString();
            ResponseEntity<String> responseMeta = restTemplate.exchange(urlWithParamMeta, HttpMethod.GET, HttpEntity.EMPTY, String.class);
            String responseBodyMeta = responseMeta.getBody();
            ObjectMapper objectMapperMeta = new ObjectMapper();
            try {
                JsonNode jsonNodeMeta = objectMapperMeta.readTree(responseBodyMeta);
                int articleCount = jsonNodeMeta.get("meta").get("count").asInt();
                int pageCount = (int)Math.ceil(articleCount/25.0);
                System.out.println(pageCount);
                for(int pageCo = 1; pageCo<=pageCount; pageCo++){
                    System.out.println("page: "+pageCo);
                    String urlPage = "https://api.openalex.org/works?per-page=25&filter=author.id:" + id + "&page="+pageCo;
                    System.out.println(urlPage);
                    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(urlPage);
                    String urlWithParam = uriBuilder.toUriString();
                    ResponseEntity<String> response = restTemplate.exchange(urlWithParam, HttpMethod.GET, HttpEntity.EMPTY, String.class);
                    String responseBody = response.getBody();
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    if (jsonNode != null) {
                        for (int i = 0; i< jsonNode.get("results").size();i++) {
                            System.out.println("------------------------------------------------------");
                            System.out.println("article: " +i);
                            JsonNode resultNode = jsonNode.get("results").get(i);
                            JsonNode doiNode = resultNode.get("doi");
                            String doi;
                            if (doiNode != null && !doiNode.isNull()) {
                                doi = doiNode.asText().substring("https://doi.org/".length());
                                System.out.println("doi: " + doi);
                            } else {
                                System.out.println("DOI is null or empty, skipping this article.");
                                continue;
                            }

                            Optional<ScientificArticle> article = scientificArticleRepository.findByDoi(doi);
                            // adding new article
                            if (article.isEmpty()) {
                                ScientificArticle newArticle = new ScientificArticle();
                                newArticle.setDoi(doi);
                                newArticle.setArticleTitle(resultNode.get("title").asText());
                                System.out.println("title: " + resultNode.get("title").asText());
                                newArticle.setPublicationDate(Timestamp.valueOf(resultNode.get("publication_date").asText() + " 00:00:00"));
                                System.out.println("date: " + resultNode.get("publication_date").asText());
                                newArticle.setCitationCount(resultNode.get("cited_by_count").asInt());
                                System.out.println("citation: " + resultNode.get("cited_by_count").asText());
                                System.out.println("open_access: "+ resultNode.get("open_access").get("is_oa").asBoolean());
                                if (resultNode.get("open_access").get("is_oa").asBoolean()) {
                                    newArticle.setOpenAccess(true);
                                    newArticle.setPaperPdf(resultNode.get("open_access").get("oa_url").asText());
                                } else {
                                    newArticle.setOpenAccess(false);
                                }
                                newArticle.setSource("open_alex");
                                scientificArticleRepository.save(newArticle);
                                System.out.println("created article");
                                scientificArticleLogsRepostory.save(new ScientificArticleLogs(newArticle, "created"));

                                //adding article author
                                for (JsonNode authorNode : resultNode.get("authorships")){
                                    System.out.println("Author");
                                    ArticleAuthor articleAuthor = new ArticleAuthor();
                                    articleAuthor.setScientificArticle(newArticle);
                                    String openAlexId = authorNode.get("author").get("id").asText().substring(authorNode.get("author").get("id").asText().lastIndexOf("/") + 1);
                                    System.out.println("id: "+openAlexId);
                                    FacultyMember facultyMember = facultyMemberRepository.findByOpenAlexId(openAlexId);
                                    if(facultyMember == null){
                                        ExternalFacultyMember externalFacultyMember = externalFacultyMemberRepository.findByOpenAlexId(openAlexId);
                                        articleAuthor.setIsFacultyMember(false);
                                        if(externalFacultyMember == null){
                                            ExternalFacultyMember newExtMember = new ExternalFacultyMember();
                                            newExtMember.setOpenAlexId(openAlexId);
                                            newExtMember.setAuthorName(authorNode.get("author").get("display_name").asText());
                                            System.out.println("id: "+openAlexId);
                                            JsonNode affNode = authorNode.get("institutions");
                                            if(!affNode.isEmpty()) {
                                                newExtMember.setAffiliation(affNode.get(0).get("display_name").asText());
                                                System.out.println("inst: "+affNode.get(0).get("display_name").asText());
                                            }
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
                                    articleAuthor.setPublicationDate(newArticle.getPublicationDate());
                                    articleAuthor.setCitationCount(newArticle.getCitationCount());
                                    articleAuthorRepository.save(articleAuthor);

                                }
                            } else { // updating citation count for existing article
                                ScientificArticle article1 = article.get();
                                if(Objects.equals(article1.getSource(), "open_alex")){
                                    int citCount = resultNode.get("cited_by_count").asInt();
                                    System.out.println("updating citation: "+ citCount);
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
                    }
                }
            }catch (Exception e) {throw new Exception(e);}
            new ResponseEntity<>("Article Synchronization Completed.", HttpStatus.OK);
        }catch(Exception e){
            new ResponseEntity<>("Check the Request.", HttpStatus.BAD_REQUEST);

        }
    }


    public void updateArticlesWithSemantic(int id) {
        try{
            String url = "https://api.semanticscholar.org/graph/v1/author/" + id + "?fields=url,name,affiliations,paperCount,externalIds,citationCount,hIndex,papers,papers.paperId,papers.isOpenAccess,papers.openAccessPdf,papers.externalIds,papers.title,papers.authors,papers.fieldsOfStudy,papers.publicationDate,papers.citationCount";
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);
            String urlWithParam = uriBuilder.toUriString();
            ResponseEntity<String> response = restTemplate.exchange(urlWithParam, HttpMethod.GET, HttpEntity.EMPTY, String.class);
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode jsonNode= objectMapper.readTree(responseBody);
                if (jsonNode != null) {
                    if (!jsonNode.get("papers").isEmpty()){
                        for (int i = 0; i < jsonNode.get("papers").size(); i++) {
                            System.out.println("------------------------------------------------------");
                            System.out.println("article: " + i);
                            JsonNode resultNode = jsonNode.get("papers").get(i);
                            JsonNode doiNode = resultNode.get("externalIds").get("DOI");
                            String doi;
                            if (doiNode != null && !doiNode.isNull()) {
                                doi = doiNode.asText();
                                System.out.println("doi: " + doi);
                            } else {
                                System.out.println("DOI is null or empty, skipping this article.");
                                continue;
                            }

                            Optional<ScientificArticle> article = scientificArticleRepository.findByDoi(doi);
                            // adding new article
                            if (article.isEmpty()) {
                                ScientificArticle newArticle = new ScientificArticle();
                                newArticle.setDoi(doi);
                                newArticle.setArticleTitle(resultNode.get("title").asText());
                                System.out.println("title: " + resultNode.get("title").asText());
                                newArticle.setPublicationDate(Timestamp.valueOf(resultNode.get("publicationDate").asText() + " 00:00:00"));
                                System.out.println("date: " + resultNode.get("publicationDate").asText());
                                newArticle.setCitationCount(resultNode.get("citationCount").asInt());
                                System.out.println("citation: " + resultNode.get("citationCount").asText());
                                System.out.println("open_access: " + resultNode.get("isOpenAccess").asBoolean());
                                if (resultNode.get("isOpenAccess").asBoolean()) {
                                    newArticle.setOpenAccess(true);
                                    newArticle.setPaperPdf(resultNode.get("openAccessPdf").asText());
                                } else {
                                    newArticle.setOpenAccess(false);
                                }
                                newArticle.setSource("semantic");
                                scientificArticleRepository.save(newArticle);
                                System.out.println("created article");
                                scientificArticleLogsRepostory.save(new ScientificArticleLogs(newArticle, "created"));

                                //adding article author
                                for (JsonNode authorNode : resultNode.get("authors")) {
                                    System.out.println("Author");
                                    ArticleAuthor articleAuthor = new ArticleAuthor();
                                    articleAuthor.setScientificArticle(newArticle);
                                    int semanticId = authorNode.get("authorId").asInt();
                                    System.out.println("id: " + semanticId);
                                    FacultyMember facultyMember = facultyMemberRepository.findBySemanticId(semanticId);
                                    if (facultyMember == null) {
                                        ExternalFacultyMember externalFacultyMember = externalFacultyMemberRepository.findBySemanticId(semanticId);
                                        articleAuthor.setIsFacultyMember(false);
                                        if (externalFacultyMember == null) {
                                            ExternalFacultyMember newExtMember = new ExternalFacultyMember();
                                            newExtMember.setSemanticId(semanticId);
                                            newExtMember.setAuthorName(authorNode.get("name").asText());
                                            System.out.println("id: " + semanticId);

                                            String urlAuthor = "https://api.semanticscholar.org/graph/v1/author/" + semanticId + "?fields=url,name,affiliations";
                                            UriComponentsBuilder uriBuilderAuthor = UriComponentsBuilder.fromUriString(urlAuthor);
                                            String urlWithParamAuthor = uriBuilderAuthor.toUriString();
                                            ResponseEntity<String> responseAuthor = restTemplate.exchange(urlWithParamAuthor, HttpMethod.GET, HttpEntity.EMPTY, String.class);
                                            String responseBodyAuthor = responseAuthor.getBody();
                                            ObjectMapper objectMapperAuthor = new ObjectMapper();
                                            JsonNode jsonNodeAuthor= objectMapperAuthor.readTree(responseBodyAuthor);
                                            if (jsonNodeAuthor != null) {
                                                if(!jsonNodeAuthor.get("affiliations").isEmpty()){
                                                    newExtMember.setAffiliation(jsonNodeAuthor.get("affiliations").get(0).asText());
                                                    System.out.println("inst: " + jsonNodeAuthor.get("affiliations").get(0).asText());
                                                }
                                            }
                                            externalFacultyMemberRepository.save(newExtMember);
                                            articleAuthor.setIsFacultyMember(false);
                                            articleAuthor.setAuthorId(newExtMember.getExternalAuthorId());
                                        } else {
                                            articleAuthor.setAuthorId(externalFacultyMember.getExternalAuthorId());
                                        }
                                    } else {
                                        articleAuthor.setIsFacultyMember(true);
                                        articleAuthor.setAuthorId(facultyMember.getAuthorId());
                                    }
                                    articleAuthor.setPublicationDate(newArticle.getPublicationDate());
                                    articleAuthor.setCitationCount(newArticle.getCitationCount());
                                    articleAuthorRepository.save(articleAuthor);

                                }
                            } else { // updating citation count for existing article
                                ScientificArticle article1 = article.get();
                                if(Objects.equals(article1.getSource(), "semantic")){
                                    int citCount = resultNode.get("citationCount").asInt();
                                    System.out.println("updating citation: " + citCount);
                                    if (article1.getCitationCount() != citCount) {
                                        int coutn = article1.getCitationCount();
                                        article1.setCitationCount(resultNode.get("citationCount").asInt());
                                        scientificArticleRepository.save(article1);
                                        ScientificArticleLogs log = new ScientificArticleLogs(article1, "updated citation count", coutn, resultNode.get("cited_by_count").asInt());
                                        scientificArticleLogsRepostory.save(log);
                                    }
                                }

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
                updateArticlesWithSemantic(member.getSemanticId());
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

