package com.uni.research_portal.service;

import com.uni.research_portal.dto.*;
import com.uni.research_portal.model.*;
import com.uni.research_portal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;

import static com.uni.research_portal.util.Jwt.extractSubject;

@Service
public class DepartmentService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    FacultyRepository facultyRepository;

    @Autowired
    FacultyMemberRepository facultyMemberRepository;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    ResearchAreaRepository researchAreaRepository;

    @Autowired
    ResearchAreaAuthorRepository researchAreaAuthorRepository;

    @Autowired
    InstitutionRepository institutionRepository;

    @Autowired
    ArticleAuthorRepository articleAuthorRepository;

    @Autowired
    ScientificArticleRepository scientificArticleRepository;

    @Autowired
    ExternalFacultyMemberRepository externalFacultyMemberRepository;



    public Department createDepartment(CreateDepartmentDto dto, String token){
        if (adminRepository.countByEmail(extractSubject(token))>0){
            Faculty fac = facultyRepository.findById(dto.getFacultyId()).get();
            Department newDepartment = new Department(fac, dto.getDepartmentName());
            departmentRepository.save(newDepartment);
            return newDepartment;
        }
        else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<String> deleteDepartment(int id, String token){
        if (adminRepository.countByEmail(extractSubject(token))>0){
            departmentRepository.deleteById(id);
            return new ResponseEntity<>("Department is deleted", HttpStatus.OK);
        }
        else{
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }

    public List<Department> getDepartments(){
        List<Department> departments = departmentRepository.findAll();
        return departments;
    }

    public List<ResearchAreaDto> getResearchAreas(int id){
        Department department = departmentRepository.findByDepartmentId(id).get();
        List<FacultyMember> members = facultyMemberRepository.findByDepartmentIdDepartmentId(department.getDepartmentId());
        List<ResearchAreaAuthor> researchAreaAuthors = researchAreaAuthorRepository.findByAuthorIdIn(members);

        Map<Integer, Integer> researchAreaCountMap = researchAreaAuthors.stream()
                .collect(Collectors.groupingBy(
                        author -> author.getResearchAreaId().getResearchAreaId(),
                        Collectors.summingInt(ResearchAreaAuthor::getCount)
                ));

        List<ResearchAreaDto> response = new ArrayList<>();
        researchAreaCountMap.forEach((researchAreaId, count) -> {

            String fingerprintName = researchAreaAuthors.stream()
                    .filter(author -> author.getResearchAreaId().getResearchAreaId() == researchAreaId)
                    .findFirst()
                    .map(author -> author.getResearchAreaId().getFingerprintName())
                    .orElse(""); // Default to empty string if no matching author found

            ResearchAreaDto dto = new ResearchAreaDto(
                    fingerprintName,
                    researchAreaId,
                    count
            );
            response.add(dto);
        });

        response.sort(Comparator.comparingInt(ResearchAreaDto::getCount).reversed());
        if (response.size() > 10) {
            return response.subList(0, 10);
        }
        return response;
    }

    public List<InstitutionDto> getInstitutions(int departmentId) {

        List<FacultyMember> facultyMembers = facultyMemberRepository.findByDepartmentIdDepartmentId(departmentId);
        List<Integer> facultyMemberIds = new ArrayList<>();
        for (FacultyMember fm: facultyMembers){
            facultyMemberIds.add(fm.getAuthorId());
        }
        Set<ArticleAuthor> articleAuthorsPage = articleAuthorRepository.findByIsFacultyMemberTrueAndAuthorIdIn(facultyMemberIds);
        Set<ScientificArticle> scientificArticles = articleAuthorsPage.stream()
                .map(ArticleAuthor::getScientificArticle)
                .collect(Collectors.toSet());


        Set<ArticleAuthor> articleAuthors = articleAuthorRepository.findByIsFacultyMemberFalseAndScientificArticleIn(scientificArticles);
        Set<Integer> externalFacultyMemberIds = articleAuthors.stream()
                .map(ArticleAuthor::getAuthorId)
                .collect(Collectors.toSet());

        Set<ExternalFacultyMember> members = externalFacultyMemberRepository.findByExternalAuthorIdIn(externalFacultyMemberIds);

        Map<Integer, Long> articleCounts = articleAuthors.stream()
                .collect(Collectors.groupingBy(ArticleAuthor::getAuthorId, Collectors.counting()));

        Map<Integer, Long> institutionCounts = members.stream()
            .filter(member -> member.getInstitutionId() != null) // Filter out members with null institutionId
            .collect(Collectors.groupingBy(
                    member -> member.getInstitutionId().getInstitutionId(),
                    Collectors.summingLong(member -> articleCounts.getOrDefault(member.getExternalAuthorId(), 0L))
            ));


        Set<Institution> institutions = members.stream()
                .map(ExternalFacultyMember::getInstitutionId)
                .filter(institutionId -> institutionId != null)
                .collect(Collectors.toSet());

        return institutions.stream()
                .map(institution -> new InstitutionDto(
                        institution.getInstitutionId(),
                        institution.getName(),
                        institution.getCountry(),
                        institution.getXCoordinate(),
                        institution.getYCoordinate(),
                        institutionCounts.getOrDefault(institution.getInstitutionId(), 0L).intValue()
                ))
                .collect(Collectors.toList());
    }


    public List<CountryArticleCountDto> getTotalArticlesAndAverageCoordinatesByCountry(int departmentId) {
        List<FacultyMember> facultyMembers = facultyMemberRepository.findByDepartmentIdDepartmentId(departmentId);
        List<Integer> facultyMemberIds = new ArrayList<>();
        for (FacultyMember fm: facultyMembers){
            facultyMemberIds.add(fm.getAuthorId());
        }
        Set<ArticleAuthor> articleAuthorsPage = articleAuthorRepository.findByIsFacultyMemberTrueAndAuthorIdIn(facultyMemberIds);
        Set<ScientificArticle> scientificArticles = articleAuthorsPage.stream()
                .map(ArticleAuthor::getScientificArticle)
                .collect(Collectors.toSet());

        Set<ArticleAuthor> articleAuthors = articleAuthorRepository.findByIsFacultyMemberFalseAndScientificArticleIn(scientificArticles);
        Set<Integer> externalFacultyMemberIds = articleAuthors.stream()
                .map(ArticleAuthor::getAuthorId)
                .collect(Collectors.toSet());

        Set<ExternalFacultyMember> members = externalFacultyMemberRepository.findByExternalAuthorIdIn(externalFacultyMemberIds);

        Map<Integer, Long> articleCounts = articleAuthors.stream()
                .collect(Collectors.groupingBy(ArticleAuthor::getAuthorId, Collectors.counting()));

        Map<Integer, Long> institutionCounts = members.stream()
                .filter(member -> member.getInstitutionId() != null) // Filter out members with null institutionId
                .collect(Collectors.groupingBy(
                        member -> member.getInstitutionId().getInstitutionId(),
                        Collectors.summingLong(member -> articleCounts.getOrDefault(member.getExternalAuthorId(), 0L))
                ));


        Set<Institution> institutions = members.stream()
                .map(ExternalFacultyMember::getInstitutionId)
                .filter(institutionId -> institutionId != null)
                .collect(Collectors.toSet());

        Map<String, CountryStatistics> countryStatisticsMap = new HashMap<>();

        for (Institution institution : institutions) {
            String country = institution.getCountry();
            Long articleCount = institutionCounts.getOrDefault(institution.getInstitutionId(), 0L);

            countryStatisticsMap.computeIfAbsent(country, k -> new CountryStatistics()).addInstitution(
                    institution.getXCoordinate(),
                    institution.getYCoordinate(),
                    articleCount
            );
        }

        return countryStatisticsMap.entrySet().stream()
                .map(entry -> {
                    String country = entry.getKey();
                    CountryStatistics stats = entry.getValue();
                    return new CountryArticleCountDto(
                            country,
                            stats.getTotalArticles(),
                            stats.getAverageLatitude(),
                            stats.getAverageLongitude()
                    );
                })
                .collect(Collectors.toList());
    }


}
