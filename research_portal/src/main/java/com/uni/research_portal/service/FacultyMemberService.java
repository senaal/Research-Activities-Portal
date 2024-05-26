package com.uni.research_portal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uni.research_portal.dto.AuthorInfoDto;
import com.uni.research_portal.dto.CreateAuthorRequestDto;
import com.uni.research_portal.dto.DepartmentMembers;
import com.uni.research_portal.exception.BadRequestException;
import com.uni.research_portal.model.Citations;
import com.uni.research_portal.model.Department;
import com.uni.research_portal.model.FacultyMember;
import com.uni.research_portal.model.FacultyMemberLogs;

import com.uni.research_portal.dto.ResearchAreaDto;
import com.uni.research_portal.model.*;
import com.uni.research_portal.repository.*;
import com.uni.research_portal.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.uni.research_portal.util.Jwt.extractSubject;

@Service
public class FacultyMemberService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    FacultyMemberRepository facultyMemberRepository;

    @Autowired
    ArticleAuthorRepository articleAuthorRepository;

    @Autowired
    ProjectAuthorRepository projectAuthorRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    FacultyMemberLogsRepository facultyMemberLogsRepository;

    @Autowired
    ResearchAreaRepository researchAreaRepository;

    @Autowired
    ResearchAreaAuthorRepository researchAreaAuthorRepository;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    CitationsRepository citationsRepository;
    public void updateMembers(String id) {
        try{
            String url = "https://api.openalex.org/authors/" + id;
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url)
                    .queryParam("select", "display_name,cited_by_count,summary_stats,counts_by_year");
            String urlWithParam = uriBuilder.toUriString();
            ResponseEntity<String> response = restTemplate.exchange(urlWithParam, HttpMethod.GET, HttpEntity.EMPTY, String.class);
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            FacultyMember facultyMember = facultyMemberRepository.findByOpenAlexId(id);
            try {
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                if (jsonNode != null) {
                    int citedByCount = jsonNode.get("cited_by_count").asInt();
                    if(facultyMember.getCitedByCount() != citedByCount){
                        FacultyMemberLogs facultyMemberLogs = new FacultyMemberLogs(facultyMember, "updated cited by count", facultyMember.getCitedByCount(), citedByCount);

                        facultyMember.setCitedByCount(citedByCount);
                        facultyMemberLogsRepository.save(facultyMemberLogs);
                    }
                    int hIndex = jsonNode.get("summary_stats").get("h_index").asInt();
                    if(facultyMember.getHIndex() != hIndex){
                        FacultyMemberLogs facultyMemberLogs = new FacultyMemberLogs(facultyMember, "updated h-index", facultyMember.getHIndex(), hIndex);
                        facultyMember.setHIndex(hIndex);
                        facultyMemberLogsRepository.save(facultyMemberLogs);
                    }
                    int i10 = jsonNode.get("summary_stats").get("i10_index").asInt();
                    if(facultyMember.getI10Index() != i10){
                        FacultyMemberLogs facultyMemberLogs = new FacultyMemberLogs(facultyMember, "updated i10-index", facultyMember.getI10Index(), i10);
                        facultyMember.setI10Index(i10);
                        facultyMemberLogsRepository.save(facultyMemberLogs);

                    }
                    facultyMemberRepository.save(facultyMember);

                    if (!jsonNode.get("counts_by_year").isEmpty()){
                        for (int i = 0; i < jsonNode.get("counts_by_year").size(); i++) {
                            int year = jsonNode.get("counts_by_year").get(i).get("year").asInt();
                            int works_count = jsonNode.get("counts_by_year").get(i).get("works_count").asInt();
                            int cited_by_count = jsonNode.get("counts_by_year").get(i).get("cited_by_count").asInt();

                            if(citationsRepository.findByAuthorIdAndYear(facultyMember.getAuthorId(),year)==null){
                                Citations c = new Citations();
                                c.setAuthorId(facultyMember.getAuthorId());
                                c.setYear(year);
                                c.setWorkCount(works_count);
                                c.setCitedByCount(cited_by_count);
                                citationsRepository.save(c);
                            }
                            else{
                                Citations citation =  citationsRepository.findByAuthorIdAndYear(facultyMember.getAuthorId(), year);
                                if(citation.getCitedByCount()!=cited_by_count){
                                    citation.setCitedByCount(cited_by_count);
                                    citationsRepository.save(citation);
                                }
                                if(citation.getWorkCount()!=works_count){
                                    citation.setWorkCount(works_count);
                                    citationsRepository.save(citation);
                                }
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}
            new ResponseEntity<>("Synchronization Completed.", HttpStatus.OK);
        }catch(Exception e){
            new ResponseEntity<>("Check the Request.", HttpStatus.BAD_REQUEST);

        }
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "GMT+3")
    public ResponseEntity<String> syncFacultyMembers() {
        try{
        List<FacultyMember> members = facultyMemberRepository.findByIsDeletedFalse();
        for (FacultyMember member : members) {
            updateMembers(member.getOpenAlexId());
        }
        return new ResponseEntity<>("Synchronization Completed.", HttpStatus.OK);
    }catch(Exception e) {
            return new ResponseEntity<>("Check the Request.", HttpStatus.BAD_REQUEST);
        }
    }

    public AuthorInfoDto getAuthorInfo(int authorId) {
        Optional<FacultyMember> member = facultyMemberRepository.findByAuthorIdAndIsDeletedFalse(authorId);
        if(member.isPresent()){
            AuthorInfoDto response = new AuthorInfoDto();
            response.setMember(member.get());
            int articleCount = articleAuthorRepository.countByAuthorId(member.get().getAuthorId());
            int projectCount = projectAuthorRepository.countByAuthorId(member.get().getAuthorId());
            int researchAreaCount = researchAreaAuthorRepository.countByAuthorId(member.get());
            response.setNumberOfArticles(articleCount);
            response.setNumberOfProjects(projectCount);
            response.setNumberOfResearchAreas(researchAreaCount);
            return response;

        }
        else{
            throw new ResourceNotFoundException(String.format("Faculty member with id %s is not found", authorId));
        }
    }

    public List<DepartmentMembers> getMembers() {
        ArrayList<DepartmentMembers> response = new ArrayList<>();
        List<Department> departments = departmentRepository.findAll();
        for(Department department: departments) {
            List<FacultyMember> members = facultyMemberRepository.findByDepartmentIdAndIsDeletedFalse(department);
            DepartmentMembers deptMem = new DepartmentMembers();
            deptMem.setDepartment(department);
            deptMem.setMembers(members);
            response.add(deptMem);
        }
        return response;
    }

    public List<FacultyMember> getAllMembers() {
        List<FacultyMember> members = facultyMemberRepository.findByIsDeletedFalse();
        return members;
    }

    public Optional<String> getOpenAlexIdApi(String name) {
        try {
            String url = "https://api.openalex.org/authors";
            String filterValue = "display_name.search:"+name;
            URI uri = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("filter", filterValue)
                    .build()
                    .encode()
                    .toUri();
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, HttpEntity.EMPTY, String.class);
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                if (jsonNode != null) {
                    JsonNode results = jsonNode.get("results");
                    if (results.isArray() && !results.isEmpty()) {
                        JsonNode firstResult = results.get(0);
                        String openAlexIdUrl = firstResult.get("id").asText();
                        String openAlexId = openAlexIdUrl.replace("https://openalex.org/", "");
                        return Optional.of(openAlexId);
                    } else {
                        return Optional.empty();
                    }
                }
            }catch(Exception e){
                new ResponseEntity<>("Check the Request.", HttpStatus.BAD_REQUEST);
                return Optional.empty();
            }

        }
        catch(Exception e){
            new ResponseEntity<>("Check the Request.", HttpStatus.BAD_REQUEST);
            return Optional.empty();
        }
        return Optional.empty();
    }
    public Optional<Integer> getSemanticScholarIdApi(String name) {
        try {
            String formattedName = name.replace(" ", "+");
            String url = "https://api.semanticscholar.org/graph/v1/author/search";
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url)
                    .queryParam("query", formattedName);

            String urlWithParam = uriBuilder.toUriString();
            ResponseEntity<String> response = restTemplate.exchange(urlWithParam, HttpMethod.GET, HttpEntity.EMPTY, String.class);
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode data = root.path("data");
            if (data.isArray() && !data.isEmpty()) {
                JsonNode firstResult = data.get(0);
                Integer authorId = firstResult.path("authorId").asInt();
                return Optional.of(authorId);
            } else {
                return Optional.empty();
            }

        } catch (Exception e) {
            new ResponseEntity<>("Check the Request.", HttpStatus.BAD_REQUEST);
            return Optional.empty();
        }
    }


    public FacultyMember createFacultyMember(CreateAuthorRequestDto createAuthorRequestDto, String token){
            Department department = departmentRepository.findByDepartmentId(createAuthorRequestDto.getDepartmentId()).get();
            FacultyMember newMember = new FacultyMember();
            newMember.setDepartmentId(department);
            newMember.setAuthorName(createAuthorRequestDto.getAuthorName());
            newMember.setEmail(createAuthorRequestDto.getEmail());
            newMember.setPhone(createAuthorRequestDto.getPhone());
            newMember.setAddress(createAuthorRequestDto.getAddress());
            newMember.setTitle(createAuthorRequestDto.getTitle());
            newMember.setPhoto(createAuthorRequestDto.getPhoto());
            Optional<String> openAlex = getOpenAlexIdApi(createAuthorRequestDto.getAuthorName());
            Optional<Integer> semantic = getSemanticScholarIdApi(createAuthorRequestDto.getAuthorName());
            if(openAlex.isEmpty() || semantic.isEmpty()){
                throw new BadRequestException("There is no ID found for provided name!");
            }
            else{
                newMember.setOpenAlexId(openAlex.get());
                newMember.setSemanticId(semantic.get());
                facultyMemberRepository.save(newMember);
                FacultyMemberLogs facultyMemberLogs = new FacultyMemberLogs(newMember, "created");
                facultyMemberLogsRepository.save(facultyMemberLogs);
                return newMember;
            }
    }

    public FacultyMember deleteFacultyMember(int id, String token){
        if (adminRepository.countByEmail(extractSubject(token))>0){
            FacultyMember deletedMember = facultyMemberRepository.findByAuthorIdAndIsDeletedFalse(id).get();
            deletedMember.setDeleted(true);
            facultyMemberRepository.save(deletedMember);
            FacultyMemberLogs facultyMemberLogs = new FacultyMemberLogs(deletedMember, "deleted");
            facultyMemberLogsRepository.save(facultyMemberLogs);
            return deletedMember;
        }
    else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }

    public FacultyMember editFacultyMember(CreateAuthorRequestDto createAuthorRequestDto, int id, String token){
        if (adminRepository.countByEmail(extractSubject(token))>0){
            FacultyMember editedMember = facultyMemberRepository.findByAuthorIdAndIsDeletedFalse(id).get();
            editedMember.setEmail(createAuthorRequestDto.getEmail());
            editedMember.setPhone(createAuthorRequestDto.getPhone());
            editedMember.setAddress(createAuthorRequestDto.getAddress());
            editedMember.setTitle(createAuthorRequestDto.getTitle());
            editedMember.setPhoto(createAuthorRequestDto.getPhoto());
            facultyMemberRepository.save(editedMember);
            FacultyMemberLogs facultyMemberLogs = new FacultyMemberLogs(editedMember, "edited");
            facultyMemberLogsRepository.save(facultyMemberLogs);
            return editedMember;
        }
        else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<String> addResearchAreas(){
        try{
            List<FacultyMember> members = facultyMemberRepository.findByIsDeletedFalse();
            for (FacultyMember member : members) {
                String id = member.getOpenAlexId();
                String url = "https://api.openalex.org/authors/" + id;
                UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);
                String urlWithParam = uriBuilder.toUriString();
                ResponseEntity<String> response = restTemplate.exchange(urlWithParam, HttpMethod.GET, HttpEntity.EMPTY, String.class);
                String responseBody = response.getBody();
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    if (jsonNode != null) {
                        JsonNode xConcepts = jsonNode.get("topics");
                        if(!xConcepts.isEmpty()){
                            for(JsonNode concept : xConcepts) {
                                if(concept.get("count").asInt() >= 3 ){
                                    String conceptId = concept.get("id").asText();
                                    ResearchArea area = researchAreaRepository.findByOpenAlexId(conceptId);
                                    if( area == null) {
                                        area = new ResearchArea();

                                    }
                                    area.setFingerprintName(concept.get("display_name").asText());
                                    area.setOpenAlexId(conceptId);
                                    researchAreaRepository.save(area);
                                    ResearchAreaAuthor areaAuthor = new ResearchAreaAuthor();
                                    areaAuthor.setAuthorId(member);
                                    areaAuthor.setResearchAreaId(area);
                                    areaAuthor.setCount(concept.get("count").asInt());
                                    researchAreaAuthorRepository.save(areaAuthor);
                                }

                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            return new ResponseEntity<>("Research Areas Are Added.", HttpStatus.OK);
        }catch(Exception e) {
            return new ResponseEntity<>("Check the Request.", HttpStatus.BAD_REQUEST);
        }
    }

    public List<ResearchAreaDto> getResearchAreas(int id){
        FacultyMember member = facultyMemberRepository.findByAuthorIdAndIsDeletedFalse(id).get();
        List<ResearchAreaAuthor> researchAreaAuthors = researchAreaAuthorRepository.findByAuthorId(member);
        List<ResearchAreaDto> response = new ArrayList<>();
        for(ResearchAreaAuthor areaAuthor : researchAreaAuthors){
            ResearchArea area = areaAuthor.getResearchAreaId();
            ResearchAreaDto dto = new ResearchAreaDto(area.getFingerprintName(),area.getResearchAreaId(),areaAuthor.getCount());
            response.add(dto);
        }
        response.sort((dto1, dto2) -> Double.compare(dto2.getCount(), dto1.getCount()));
        return response;
    }


}

