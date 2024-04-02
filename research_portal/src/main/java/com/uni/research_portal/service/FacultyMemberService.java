package com.uni.research_portal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uni.research_portal.model.FacultyMember;
import com.uni.research_portal.repository.FacultyMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
@Service
public class FacultyMemberService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    FacultyMemberRepository facultyMemberRepository;
    public void updateMembers(String id) {
        try{
            String url = "https://api.openalex.org/authors/" + id;
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url)
                    .queryParam("select", "display_name,cited_by_count,summary_stats");
            String urlWithParam = uriBuilder.toUriString();
            ResponseEntity<String> response = restTemplate.exchange(urlWithParam, HttpMethod.GET, HttpEntity.EMPTY, String.class);
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            FacultyMember facultyMember = facultyMemberRepository.findByOpenAlexId(id);
            try {
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                if (jsonNode != null) {
                    facultyMember.setCitedByCount(jsonNode.get("cited_by_count").asInt());
                    facultyMember.setHIndex(jsonNode.get("summary_stats").get("h_index").asInt());
                    facultyMember.setI10Index(jsonNode.get("summary_stats").get("i10_index").asInt());
                    System.out.println(jsonNode.get("display_name"));
                    facultyMemberRepository.save(facultyMember);
                }
            } catch (Exception ignored) {}
            new ResponseEntity<>("Synchronization Completed.", HttpStatus.OK);
        }catch(Exception e){
            new ResponseEntity<>("Check the Request.", HttpStatus.BAD_REQUEST);

        }
    }

    public ResponseEntity<String> syncFacultyMembers() {
        try{
            List<FacultyMember> members = facultyMemberRepository.findByIsDeletedFalse();
            for (FacultyMember member : members) {
                updateMembers(member.getOpenAlexId());
            }
            return new ResponseEntity<>("Synchronization Completed.", HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>("Check the Request.",HttpStatus.BAD_REQUEST);

        }
    }
}