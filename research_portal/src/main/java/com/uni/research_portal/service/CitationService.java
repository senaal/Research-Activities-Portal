package com.uni.research_portal.service;

import com.uni.research_portal.model.Citations;
import com.uni.research_portal.model.Department;
import com.uni.research_portal.model.FacultyMember;
import com.uni.research_portal.repository.CitationsRepository;
import com.uni.research_portal.repository.DepartmentRepository;
import com.uni.research_portal.repository.FacultyMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CitationService {

    @Autowired
    private CitationsRepository citationsRepository;
    @Autowired
    FacultyMemberRepository facultyMemberRepository;
    @Autowired
    DepartmentRepository departmentRepository;

    public Map<String, List<Integer>> getYearsAndCitationsByAuthorId(Integer authorId) {
        List<Citations> citationsList = citationsRepository.findByAuthorId(authorId);
        List<Integer> yearsList = new ArrayList<>();
        List<Integer> citationsCountList = new ArrayList<>();
        citationsList.sort(Comparator.comparingInt(Citations::getYear));

        for (Citations citation : citationsList) {
            yearsList.add(citation.getYear());
            citationsCountList.add(citation.getCitedByCount());
        }

        Map<String, List<Integer>> result = new HashMap<>();
        result.put("years", yearsList);
        result.put("citations", citationsCountList);

        return result;
    }

    public Map<String, List<Integer>> getDepartmentStatisticsByDepartmentId(Integer departmentId) {
        List<FacultyMember> facultyMembers = facultyMemberRepository.findByDepartmentIdDepartmentId(departmentId);
        Map<Integer, Integer> yearCitationsMap = new HashMap<>();

        for (FacultyMember fm : facultyMembers) {
            Integer authorId = fm.getAuthorId();
            List<Citations> citationsList = citationsRepository.findByAuthorId(authorId);
            for (Citations citation : citationsList) {
                int year = citation.getYear();
                int citationsCount = citation.getCitedByCount();
                yearCitationsMap.put(year, yearCitationsMap.getOrDefault(year, 0) + citationsCount);
            }
        }

        List<Integer> yearsList = new ArrayList<>(yearCitationsMap.keySet());
        Collections.sort(yearsList);
        List<Integer> citationsCountList = new ArrayList<>();
        for (Integer year : yearsList) {
            citationsCountList.add(yearCitationsMap.get(year));
        }

        Map<String, List<Integer>> result = new HashMap<>();
        result.put("years", yearsList);
        result.put("citations", citationsCountList);

        return result;
    }

    public Map<String, List<Integer>> getStatisticsByFacultyId(Integer facultyId) {
        List<Department> departments = departmentRepository.findDepartmentIdsByFacultyIdFacultyId(facultyId);
        List<Integer> departmentIds = new ArrayList<>();
        for (Department dept: departments){
            departmentIds.add(dept.getDepartmentId());
        }
        List<FacultyMember> facultyMembers = facultyMemberRepository.findByDepartmentIdDepartmentIdIn(departmentIds);
        Map<Integer, Integer> yearCitationsMap = new HashMap<>();

        for (FacultyMember fm : facultyMembers) {
            Integer authorId = fm.getAuthorId();
            List<Citations> citationsList = citationsRepository.findByAuthorId(authorId);
            for (Citations citation : citationsList) {
                int year = citation.getYear();
                int citationsCount = citation.getCitedByCount();
                yearCitationsMap.put(year, yearCitationsMap.getOrDefault(year, 0) + citationsCount);
            }
        }

        List<Integer> yearsList = new ArrayList<>(yearCitationsMap.keySet());
        Collections.sort(yearsList);
        List<Integer> citationsCountList = new ArrayList<>();
        for (Integer year : yearsList) {
            citationsCountList.add(yearCitationsMap.get(year));
        }

        Map<String, List<Integer>> result = new HashMap<>();
        result.put("years", yearsList);
        result.put("citations", citationsCountList);

        return result;
    }
}
