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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.uni.research_portal.util.Jwt.extractSubject;

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
    InstitutionRepository institutionRepository;

    @Autowired
    AdminRepository adminRepository;



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
                for(int pageCo = 1; pageCo<=pageCount; pageCo++){
                    String urlPage = "https://api.openalex.org/works?per-page=25&filter=author.id:" + id + "&page="+pageCo;
                    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(urlPage);
                    String urlWithParam = uriBuilder.toUriString();
                    ResponseEntity<String> response = restTemplate.exchange(urlWithParam, HttpMethod.GET, HttpEntity.EMPTY, String.class);
                    String responseBody = response.getBody();
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    if (jsonNode != null) {
                        for (int i = 0; i< jsonNode.get("results").size();i++) {
                            JsonNode resultNode = jsonNode.get("results").get(i);
                            JsonNode doiNode = resultNode.get("doi");
                            String doi;
                            if (doiNode != null && !doiNode.isNull()) {
                                doi = doiNode.asText().substring("https://doi.org/".length());
                            } else {
                                continue;
                            }

                            Optional<ScientificArticle> article = scientificArticleRepository.findByDoi(doi);
                            // adding new article
                            if (article.isEmpty()) {
                                ScientificArticle newArticle = new ScientificArticle();
                                newArticle.setDoi(doi);
                                newArticle.setArticleTitle(resultNode.get("title").asText());
                                newArticle.setPublicationDate(Timestamp.valueOf(resultNode.get("publication_date").asText() + " 00:00:00"));
                                newArticle.setCitationCount(resultNode.get("cited_by_count").asInt());
                                if (resultNode.get("open_access").get("is_oa").asBoolean()) {
                                    newArticle.setOpenAccess(true);
                                    newArticle.setPaperPdf(resultNode.get("open_access").get("oa_url").asText());
                                } else {
                                    newArticle.setOpenAccess(false);
                                }
                                newArticle.setSource("open_alex");
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
                                            JsonNode affNode = authorNode.get("institutions");
                                            if(!affNode.isEmpty()) {
                                                String ror = affNode.get(0).get("ror").asText();
                                                Institution inst = institutionRepository.findByRor(ror);
                                                if(inst == null){
                                                    String urlInstPage = "https://api.openalex.org/institutions/ror:" + ror;
                                                    UriComponentsBuilder uriInstBuilder = UriComponentsBuilder.fromUriString(urlInstPage);
                                                    String urlInstWithParam = uriInstBuilder.toUriString();
                                                    ResponseEntity<String> responseInst = restTemplate.exchange(urlInstWithParam, HttpMethod.GET, HttpEntity.EMPTY, String.class);
                                                    String responseBodyInst = responseInst.getBody();
                                                    ObjectMapper objectMapperInst = new ObjectMapper();
                                                    JsonNode jsonNodeInst = objectMapperInst.readTree(responseBodyInst);
                                                    Institution newInst = new Institution(jsonNodeInst.get("display_name").asText(),jsonNodeInst.get("geo").get("latitude").asDouble(),jsonNodeInst.get("geo").get("longitude").asDouble(),ror,jsonNodeInst.get("geo").get("country").asText() );
                                                    institutionRepository.save(newInst);
                                                    newExtMember.setInstitutionId(newInst);
                                                }else{
                                                    newExtMember.setInstitutionId(inst);
                                                }
                                            }else{
                                                Institution inst1 = institutionRepository.findById(1).get();
                                                newExtMember.setInstitutionId(inst1);

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
                            JsonNode resultNode = jsonNode.get("papers").get(i);
                            JsonNode doiNode = resultNode.get("externalIds").get("DOI");
                            String doi;
                            if (doiNode != null && !doiNode.isNull()) {
                                doi = doiNode.asText();
                            } else {
                                continue;
                            }

                            Optional<ScientificArticle> article = scientificArticleRepository.findByDoi(doi);
                            // adding new article
                            if (article.isEmpty()) {
                                ScientificArticle newArticle = new ScientificArticle();
                                newArticle.setDoi(doi);
                                newArticle.setArticleTitle(resultNode.get("title").asText());
                                newArticle.setPublicationDate(Timestamp.valueOf(resultNode.get("publicationDate").asText() + " 00:00:00"));
                                newArticle.setCitationCount(resultNode.get("citationCount").asInt());
                                if (resultNode.get("isOpenAccess").asBoolean()) {
                                    newArticle.setOpenAccess(true);
                                    newArticle.setPaperPdf(resultNode.get("openAccessPdf").asText());
                                } else {
                                    newArticle.setOpenAccess(false);
                                }
                                newArticle.setSource("semantic");
                                scientificArticleRepository.save(newArticle);
                                scientificArticleLogsRepostory.save(new ScientificArticleLogs(newArticle, "created"));

                                //adding article author
                                for (JsonNode authorNode : resultNode.get("authors")) {
                                    ArticleAuthor articleAuthor = new ArticleAuthor();
                                    articleAuthor.setScientificArticle(newArticle);
                                    int semanticId = authorNode.get("authorId").asInt();
                                    FacultyMember facultyMember = facultyMemberRepository.findBySemanticId(semanticId);
                                    if (facultyMember == null) {
                                        ExternalFacultyMember externalFacultyMember = externalFacultyMemberRepository.findBySemanticId(semanticId);
                                        articleAuthor.setIsFacultyMember(false);
                                        if (externalFacultyMember == null) {
                                            ExternalFacultyMember newExtMember = new ExternalFacultyMember();
                                            newExtMember.setSemanticId(semanticId);
                                            newExtMember.setAuthorName(authorNode.get("name").asText());

                                            String urlAuthor = "https://api.semanticscholar.org/graph/v1/author/" + semanticId + "?fields=url,name,affiliations";
                                            UriComponentsBuilder uriBuilderAuthor = UriComponentsBuilder.fromUriString(urlAuthor);
                                            String urlWithParamAuthor = uriBuilderAuthor.toUriString();
                                            ResponseEntity<String> responseAuthor = restTemplate.exchange(urlWithParamAuthor, HttpMethod.GET, HttpEntity.EMPTY, String.class);
                                            String responseBodyAuthor = responseAuthor.getBody();
                                            ObjectMapper objectMapperAuthor = new ObjectMapper();
                                            JsonNode jsonNodeAuthor= objectMapperAuthor.readTree(responseBodyAuthor);
                                            if (jsonNodeAuthor != null) {
                                                if(!jsonNodeAuthor.get("affiliations").isEmpty()){
                                                    Institution inst1 = institutionRepository.findById(1).get();
                                                    newExtMember.setInstitutionId(inst1);

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

    public Page<ArticleWithAuthorsDto> searchScientificArticlesByTitle(String title, String sortBy, String sortOrder, int pageNumber, int pageSize) {
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        Page<ScientificArticle> articleAuthorsPage = scientificArticleRepository.findByArticleTitleContainingIgnoreCaseAndIsRejectedFalse(title, pageable);

        return articleAuthorsPage.map(articleDto -> {
            List<ArticleAuthor> authorIds = articleAuthorRepository.findByScientificArticle(articleDto);
            List<String> authorNames = new ArrayList<>();

            for (ArticleAuthor auth : authorIds) {
                String memberName;
                if (auth.getIsFacultyMember()) {
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

    public void setIsRejected(int id,int memberId, String token){
        System.out.println(extractSubject(token));
        System.out.println(facultyMemberRepository.findByEmail(extractSubject(token)).getAuthorId());
        if (adminRepository.countByEmail(extractSubject(token))>0 || facultyMemberRepository.findByEmail(extractSubject(token)).getAuthorId() == memberId){
            ScientificArticle article = scientificArticleRepository.findById(id).get();
            article.setRejected(true);
            scientificArticleRepository.save(article);
            scientificArticleLogsRepostory.save(new ScientificArticleLogs(article,"rejected"));
        }
        else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);


        }
    }
}

