package com.uni.research_portal.repository;

import com.uni.research_portal.model.ResearchArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResearchAreaRepository extends JpaRepository<ResearchArea, Integer> {
    ResearchArea findByOpenAlexId(String id);

}
